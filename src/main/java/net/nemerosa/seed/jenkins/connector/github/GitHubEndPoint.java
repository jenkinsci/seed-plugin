package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.connector.CannotHandleRequestException;
import net.nemerosa.seed.jenkins.connector.RequestNonAuthorizedException;
import net.nemerosa.seed.jenkins.connector.UnknownRequestException;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Extension
public class GitHubEndPoint extends AbstractEndPoint {

    private static final String X_GIT_HUB_EVENT = "X-GitHub-Event";
    private static final String X_GIT_HUB_SIGNATURE = "X-Hub-Signature";

    private static final Logger LOGGER = Logger.getLogger(GitHubEndPoint.class.getName());
    private static final SeedChannel SEED_CHANNEL = SeedChannel.of("Seed GitHub end point");

    public GitHubEndPoint(SeedService seedService) {
        super(seedService);
    }

    public GitHubEndPoint() {
        super();
    }

    @Override
    public String getUrlName() {
        return "seed-github";
    }

    @Override
    protected SeedEvent extractEvent(StaplerRequest req) throws IOException {
        // Gets the event type sent by GitHub
        String ghEvent = req.getHeader(X_GIT_HUB_EVENT);
        LOGGER.finer("GitHub event: " + ghEvent);
        // Reads the content as text
        String payload = IOUtils.toString(req.getReader());
        // Parses as JSON
        JSONObject json = JSONObject.fromObject(payload);
        // Gets the project name
        String project = getProject(json);
        // Checks the signature
        checkSignature(req, payload, project);
        // Event type
        if ("create".equals(ghEvent)) {
            return createEvent(json);
        } else if ("delete".equals(ghEvent)) {
            return deleteEvent(json);
        } else if ("push".equals(ghEvent)) {
            return pushEvent(json);
        }
        // Unknown
        else {
            throw new UnknownRequestException("Unknown event: " + ghEvent);
        }
    }

    protected void checkSignature(StaplerRequest req, String payload, String project) throws UnsupportedEncodingException {
        // Gets the secret key for the project
        String secretKey = seedService.getSecretKey(project);
        if (StringUtils.isBlank(secretKey)) {
            return;
        }

        // Gets the signature header
        String ghSignature = req.getHeader(X_GIT_HUB_SIGNATURE);

        // Secret key specification
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1");

        // HMac signature
        try {
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(keySpec);

            byte[] rawHmac = mac.doFinal(payload.getBytes("UTF-8"));

            // HMac Hex signature
            String hmac = Hex.encodeHexString(rawHmac);

            // Comparison
            if (!StringUtils.equals(hmac, ghSignature)) {
                throw new RequestNonAuthorizedException();
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new CannotHandleRequestException(ex);
        } catch (InvalidKeyException ex) {
            throw new CannotHandleRequestException(ex);
        }
    }

    private SeedEvent pushEvent(JSONObject json) {
        // Gets the branch reference
        String ref = json.getString("ref");
        // Parses the branch name
        String branch = StringUtils.removeStart(ref, "refs/heads/");
        // List of commits
        JSONArray commits = json.optJSONArray("commits");
        Commits commitContext = new Commits();
        if (commits != null) {
            for (Object o : commits) {
                JSONObject commit = (JSONObject) o;
                scanCommits(commitContext, commit, "added");
                scanCommits(commitContext, commit, "removed");
                scanCommits(commitContext, commit, "modified");
            }
        }
        // Event according to the context
        if (commitContext.isOnlySeed()) {
            // Seed update only
            return new SeedEvent(
                    getProject(json),
                    branch,
                    SeedEventType.SEED,
                    SEED_CHANNEL
            );
        } else {
            // Normal push
            return new SeedEvent(
                    getProject(json),
                    branch,
                    SeedEventType.COMMIT,
                    SEED_CHANNEL
            ).withParam("commit", json.getJSONObject("head_commit").getString("id"));
        }
    }

    private void scanCommits(Commits commitContext, JSONObject commit, String mode) {
        JSONArray paths = commit.optJSONArray(mode);
        if (paths != null) {
            for (Object o : paths) {
                String path = (String) o;
                boolean seedPath = StringUtils.startsWith(path, "seed/");
                commitContext.feed(seedPath);
            }
        }
    }

    private SeedEvent createEvent(JSONObject json) {
        return branchEvent(json, SeedEventType.CREATION);
    }

    private SeedEvent deleteEvent(JSONObject json) {
        return branchEvent(json, SeedEventType.DELETION);
    }

    private SeedEvent branchEvent(JSONObject json, SeedEventType eventType) {
        // Checks the ref_type
        String ref_type = json.getString("ref_type");
        if (!"branch".equals(ref_type)) {
            return null;
        }
        // Repository ID = project
        String project = getProject(json);
        // Branch = ref
        String branch = json.getString("ref");
        // OK
        return new SeedEvent(
                project,
                branch,
                eventType,
                SEED_CHANNEL
        );
    }

    private String getProject(JSONObject json) {
        return json.getJSONObject("repository").getString("full_name");
    }

}

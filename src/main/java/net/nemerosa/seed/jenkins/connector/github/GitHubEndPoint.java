package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.connector.UnknownRequestException;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.logging.Logger;

@Extension
public class GitHubEndPoint extends AbstractEndPoint {

    private static final String X_GIT_HUB_EVENT = "X-GitHub-Event";

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

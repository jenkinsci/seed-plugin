package net.nemerosa.seed.triggering.connector.bitbucket;

import hudson.Extension;
import net.nemerosa.seed.triggering.SeedChannel;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedEventType;
import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.connector.AbstractEndPoint;
import net.nemerosa.seed.triggering.connector.RequestFormatException;
import net.nemerosa.seed.triggering.connector.UnknownRequestException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

@Extension
public class BitBucketEndPoint extends AbstractEndPoint {

    public static final String X_EVENT_KEY = "X-Event-Key";
    public static final String X_EVENT_VALUE = "repo:push";

    private static final SeedChannel SEED_CHANNEL = SeedChannel.of("bitbucket", "Seed BitBucket end point");

    public BitBucketEndPoint(SeedService seedService) {
        super(seedService);
    }

    @SuppressWarnings("unused")
    public BitBucketEndPoint() {
        super();
    }

    @Override
    protected SeedEvent extractEvent(StaplerRequest req) throws IOException {
        // Gets the event type sent by BitBucket, and accepts only repo:push events
        String eventKey = req.getHeader(X_EVENT_KEY);
        if (!StringUtils.equals(X_EVENT_VALUE, eventKey)) {
            throw new UnknownRequestException(String.format(
                    "Expected %s in %s header, but was %s",
                    X_EVENT_VALUE, X_EVENT_KEY, eventKey
            ));
        }
        // Reads the content as text
        String payload = IOUtils.toString(req.getReader());
        // Parses as JSON
        JSONObject json = JSONObject.fromObject(payload);
        // Gets the list of changes
        JSONArray changes = json.getJSONObject("push").optJSONArray("changes");
        // We request at least one change
        if (changes.size() == 0) {
            throw new RequestFormatException("At least one change is required.");
        }
        // Takes only the first change
        JSONObject change = changes.getJSONObject(0);
        // Branch boundaries
        JSONObject oldBranch = change.optJSONObject("old");
        JSONObject newBranch = change.optJSONObject("new");
        // Branch push
        if (oldBranch != null && newBranch != null) {
            return pushEvent(json, change);
        }
        // Branch creation
        else if (oldBranch == null && newBranch != null) {
            return createEvent(json, newBranch);
        }
        // Branch deletion
        else if (oldBranch != null) {
            return deleteEvent(json, oldBranch);
        }
        // No branch?
        else {
            throw new RequestFormatException("No branch was mentioned in the payload");
        }
    }

    private SeedEvent createEvent(JSONObject json, JSONObject branch) {
        return branchEvent(json, branch, SeedEventType.CREATION);
    }

    private SeedEvent deleteEvent(JSONObject json, JSONObject branch) {
        return branchEvent(json, branch, SeedEventType.DELETION);
    }

    private SeedEvent branchEvent(JSONObject json, JSONObject branch, SeedEventType eventType) {
        // Project
        String project = getProject(json);
        // Branch
        String branchName = branch.getString("name");
        // OK
        return new SeedEvent(
                project,
                branchName,
                eventType,
                SEED_CHANNEL
        );
    }

    private SeedEvent pushEvent(JSONObject json, JSONObject change) {
        // Gets the list of commits
        JSONArray commits = change.getJSONArray("commits");
        // No commit?
        if (commits.size() == 0) {
            throw new RequestFormatException("No commit was specified.");
        }
        // Scans for a seed event
        boolean seed = false;
        for (Object o : commits) {
            JSONObject commit = (JSONObject) o;
            String message = commit.getString("message");
            // TODO The BitBucket payload does not contain any information about the modified paths
            // Using the message in order to identify a seed event :(
            if (StringUtils.containsIgnoreCase(message, "seed")) {
                seed = true;
            }
        }
        // Branch name
        String branch = change.getJSONObject("new").getString("name");
        // Seed event
        if (seed) {
            return new SeedEvent(
                    getProject(json),
                    branch,
                    SeedEventType.SEED,
                    SEED_CHANNEL
            );
        }
        // Commit event
        else {
            return new SeedEvent(
                    getProject(json),
                    branch,
                    SeedEventType.COMMIT,
                    SEED_CHANNEL
            ).withParam("commit", commits.getJSONObject(0).getString("hash"));
        }
    }

    @Override
    public String getUrlName() {
        return "seed-bitbucket";
    }

    private String getProject(JSONObject json) {
        return json.getJSONObject("repository").getString("full_name");
    }
}

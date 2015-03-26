package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.connector.UnknownRequestException;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
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
//        } else if ("delete".equals(ghEvent)) {
//            type = SeedEventType.DELETION;
//        } else if ("seed".equals(ghEvent)) {
//            type = SeedEventType.SEED;
//        } else if ("commit".equals(ghEvent)) {
//            type = SeedEventType.COMMIT;
        }
        // Unknown
        else {
            throw new UnknownRequestException("Unknown event: " + ghEvent);
        }
    }

    private SeedEvent createEvent(JSONObject json) {
        // Checks the ref_type
        String ref_type = json.getString("ref_type");
        if (!"branch".equals(ref_type)) {
            return null;
        }
        // Repository ID = project
        String project = json.getJSONObject("repository").getString("full_name");
        // Branch = ref
        String branch = json.getString("ref");
        // OK
        return new SeedEvent(
                project,
                branch,
                SeedEventType.CREATION,
                SEED_CHANNEL
        );
    }

}

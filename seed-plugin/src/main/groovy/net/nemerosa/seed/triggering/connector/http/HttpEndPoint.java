package net.nemerosa.seed.triggering.connector.http;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.connector.AbstractEndPoint;
import net.nemerosa.jenkins.seed.triggering.connector.RequestNonAuthorizedException;
import net.nemerosa.jenkins.seed.triggering.connector.UnknownRequestException;
import net.nemerosa.jenkins.seed.triggering.SeedChannel;
import net.nemerosa.jenkins.seed.triggering.SeedEvent;
import net.nemerosa.jenkins.seed.triggering.SeedEventType;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

@Extension
@Deprecated
public class HttpEndPoint extends AbstractEndPoint implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(HttpEndPoint.class.getName());
    public static final String X_SEED_TOKEN = "X-Seed-Token";

    public HttpEndPoint(SeedService seedService) {
        super(seedService);
    }

    public HttpEndPoint() {
        super();
    }

    @Override
    public String getUrlName() {
        return "seed-http";
    }

    @Override
    protected SeedEvent extractEvent(StaplerRequest req) {
        // Extracts the event
        String path = req.getRestOfPath();
        if (StringUtils.startsWith(path, "/")) {
            path = path.substring(1);
        }
        LOGGER.finest("Path = " + path);
        // Event type
        SeedEventType type;
        if ("create".equals(path)) {
            type = SeedEventType.CREATION;
        } else if ("delete".equals(path)) {
            type = SeedEventType.DELETION;
        } else if ("seed".equals(path)) {
            type = SeedEventType.SEED;
        } else if ("commit".equals(path)) {
            type = SeedEventType.COMMIT;
        }
        // Unknown
        else {
            throw new UnknownRequestException("Unknown path: " + path);
        }
        // Gets the project
        String project = extractParameter(req, "project");
        // Checks the token
        checkToken(req, project);
        // Extracts the event
        SeedEvent event = new SeedEvent(
                project,
                extractParameter(req, "branch"),
                type,
                SeedChannel.of("http", "Seed HTTP end point")
        );
        // Additional parameters
        for (String parameterName : type.getParameterNames()) {
            String parameterValue = extractParameter(req, parameterName, false);
            if (parameterValue != null) {
                event = event.withParam(parameterName, parameterValue);
            }
        }
        // OK
        return event;
    }

    private void checkToken(StaplerRequest req, String project) {
        // Gets the secret key for the project
        String secretToken = seedService.getSecretKey(project, "http");
        if (StringUtils.isBlank(secretToken)) {
            return;
        }

        // Gets the token header
        String reqToken = req.getHeader(X_SEED_TOKEN);

        // Comparison
        if (!StringUtils.equals(secretToken, reqToken)) {
            throw new RequestNonAuthorizedException();
        }

    }

}

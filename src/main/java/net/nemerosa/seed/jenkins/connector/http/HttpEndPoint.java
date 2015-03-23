package net.nemerosa.seed.jenkins.connector.http;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.connector.UnknownRequestException;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

@Extension
public class HttpEndPoint extends AbstractEndPoint implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(HttpEndPoint.class.getName());

    public HttpEndPoint(SeedService seedService) {
        super(seedService);
    }

    public HttpEndPoint() {
        super();
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
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
        // Extracts the event
        SeedEvent event = new SeedEvent(
                extractParameter(req, "project"),
                extractParameter(req, "branch"),
                type
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

}

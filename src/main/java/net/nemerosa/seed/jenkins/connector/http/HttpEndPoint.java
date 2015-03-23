package net.nemerosa.seed.jenkins.connector.http;

import com.google.inject.Guice;
import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.service.SeedServiceModule;
import net.nemerosa.seed.jenkins.support.MissingParameterException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Extension
public class HttpEndPoint implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(HttpEndPoint.class.getName());

    private final SeedService seedService;

    public HttpEndPoint(SeedService seedService) {
        this.seedService = seedService;
    }

    public HttpEndPoint() {
        this(Guice.createInjector(new SeedServiceModule()).getInstance(SeedService.class));
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

    @RequirePOST
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException {
        LOGGER.finest("Incoming POST");
        String path = req.getRestOfPath();
        if (StringUtils.startsWith(path, "/")) {
            path = path.substring(1);
        }
        LOGGER.finest("Path = " + path);
        // Events?
        if ("create".equals(path)) {
            LOGGER.finer("Event: creation");
            post(req, rsp, SeedEventType.CREATION);
        } else if ("delete".equals(path)) {
            LOGGER.finer("Event: deletion");
            post(req, rsp, SeedEventType.DELETION);
        } else if ("seed".equals(path)) {
            LOGGER.finer("Event: seed");
            post(req, rsp, SeedEventType.SEED);
        } else if ("commit".equals(path)) {
            LOGGER.finer("Event: commit");
            post(req, rsp, SeedEventType.COMMIT, "commit");
        }
        // Unknown
        else {
            LOGGER.finer("Event: unknown: " + path);
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path: " + path);
        }
    }

    protected void post(StaplerRequest req, StaplerResponse rsp, SeedEventType type, String... parameters) throws IOException {
        try {
            // Extracts the request
            SeedEvent event = extractEvent(req, type, parameters);
            // Posts the event
            post(event);
        } catch (MissingParameterException ex) {
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    protected void post(SeedEvent event) {
        // Posting
        seedService.post(event);
    }

    protected SeedEvent extractEvent(StaplerRequest req, SeedEventType type, String... parameters) {
        SeedEvent event = new SeedEvent(
                extractParameter(req, "project"),
                extractParameter(req, "branch"),
                type
        );
        // Additional parameters
        for (String parameterName : parameters) {
            String parameterValue = extractParameter(req, parameterName, false);
            if (parameterValue != null) {
                event = event.withParam(parameterName, parameterValue);
            }
        }
        // OK
        return event;
    }

    protected String extractParameter(StaplerRequest req, String name) {
        return extractParameter(req, name, true);
    }

    protected String extractParameter(StaplerRequest req, String name, boolean required) {
        String value = req.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else if (required) {
            throw new MissingParameterException(name);
        } else {
            return null;
        }
    }

}

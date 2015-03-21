package net.nemerosa.seed.jenkins.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class HttpEndPoint implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(HttpEndPoint.class.getName());

    public HttpEndPoint() {
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
        }
        // Unknown
        else {
            LOGGER.finer("Event: unknown: " + path);
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path: " + path);
        }
    }

    protected void post(StaplerRequest req, StaplerResponse rsp, SeedEventType type) throws IOException {
        try {
            // Extracts the request
            SeedEvent event = extractEvent(req, type);
            // Posts the event
            post(event);
        } catch (MissingParameterException ex) {
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    protected void post(SeedEvent event) {
        // Loading the configuration
        Injector injector = Guice.createInjector(new SeedServiceModule());
        // Gets the service
        SeedService seedService = injector.getInstance(SeedService.class);
        // Logging
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(String.format(
                    "Event: project=%s, branch=%s, type=%s",
                    event.getProject(),
                    event.getBranch(),
                    event.getType()
            ));
        }
        // Posting
        seedService.post(event);
    }

    protected SeedEvent extractEvent(StaplerRequest req, SeedEventType type) {
        return new SeedEvent(
                extractParameter(req, "project"),
                extractParameter(req, "branch"),
                type
        );
    }

    protected String extractParameter(StaplerRequest req, String name) {
        String value = req.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else {
            throw new MissingParameterException(name);
        }
    }

}

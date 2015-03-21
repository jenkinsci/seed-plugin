package net.nemerosa.seed.jenkins.http;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
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
    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        LOGGER.info("Incoming POST");
        String path = req.getRestOfPath();
        LOGGER.info("Path = " + path);
        // Events?
        if ("create".equals(path)) {
            LOGGER.info("Event: creation");
            post(req, rsp, SeedEventType.CREATION);
        }
        // Unknown
        else {
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
        // Gets the configured service
        // FIXME Method net.nemerosa.seed.jenkins.rest.RestEndPoint.post

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

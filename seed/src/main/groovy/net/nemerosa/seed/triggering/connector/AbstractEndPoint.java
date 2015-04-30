package net.nemerosa.seed.triggering.connector;

import com.google.inject.Guice;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.config.MissingParameterException;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedServiceModule;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class AbstractEndPoint implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(AbstractEndPoint.class.getName());

    protected final SeedService seedService;

    public AbstractEndPoint(SeedService seedService) {
        this.seedService = seedService;
    }

    public AbstractEndPoint() {
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

    @RequirePOST
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException {
        LOGGER.finest("Incoming POST");
        try {
            // Extracts the event
            SeedEvent event = extractEvent(req);
            if (event == null) {
                LOGGER.finer("Event not accepted");
                sendError(rsp, StaplerResponse.SC_NOT_MODIFIED, "Event not accepted");
            } else {
                LOGGER.finer(
                        String.format(
                                "Event to process: project=%s, branch=%s, type=%s, parameters=%s",
                                event.getProject(),
                                event.getBranch(),
                                event.getType(),
                                event.getParameters()
                        )
                );
                // Posts the event
                post(event);
                // OK
                sendOk(rsp, event);
            }
        } catch (IOException ex) {
            throw ex;
        } catch (RequestNonAuthorizedException ex) {
            sendError(rsp, StaplerResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (Exception ex) {
            sendError(rsp, StaplerResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    protected static String extractParameter(StaplerRequest req, String name) {
        return extractParameter(req, name, true);
    }

    protected static String extractParameter(StaplerRequest req, String name, boolean required) {
        String value = req.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else if (required) {
            throw new MissingParameterException(name);
        } else {
            return null;
        }
    }

    protected abstract SeedEvent extractEvent(StaplerRequest req) throws IOException;

    protected void sendOk(StaplerResponse rsp, SeedEvent event) throws IOException {
        rsp.setStatus(getHttpCodeForEvent(event));
        rsp.setContentType("text/plain");
        rsp.getWriter().println(
                String.format(
                        "Event processed: project=%s, branch=%s, type=%s, parameters=%s",
                        event.getProject(),
                        event.getBranch(),
                        event.getType(),
                        event.getParameters()
                )
        );
    }

    protected int getHttpCodeForEvent(@SuppressWarnings("UnusedParameters") SeedEvent event) {
        return StaplerResponse.SC_ACCEPTED;
    }

    protected void sendError(StaplerResponse rsp, int httpCode, String message) throws IOException {
        rsp.setStatus(httpCode);
        rsp.setContentType("text/plain");
        rsp.getWriter().println(message);
    }

    protected void post(SeedEvent event) {
        seedService.post(event);
    }

}

package net.nemerosa.seed.triggering.connector

import com.google.inject.Guice
import hudson.model.UnprotectedRootAction
import net.nemerosa.seed.config.MissingParameterException
import net.nemerosa.seed.triggering.SeedEvent
import net.nemerosa.seed.triggering.SeedEventType
import net.nemerosa.seed.triggering.SeedService
import net.nemerosa.seed.triggering.SeedServiceModule
import net.sf.json.JSONSerializer
import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import org.kohsuke.stapler.interceptor.RequirePOST

import java.util.logging.Logger

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
        LOGGER.info("Incoming POST");
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
                // Test?
                if (event.type == SeedEventType.TEST) {
                    sendError(rsp, StaplerResponse.SC_ACCEPTED, "Test OK")
                }
                // Actual event
                else {
                    // Posts the event
                    post(event);
                    // OK
                    sendOk(rsp, event);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } catch (RequestNonAuthorizedException ex) {
            sendError(rsp, StaplerResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (Exception ex) {
            LOGGER.severe(ex.getMessage());
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

    protected static void sendOk(StaplerResponse rsp, SeedEvent event) throws IOException {
        rsp.setStatus(getHttpCodeForEvent(event));
        rsp.setContentType("application/json");
        JSONSerializer.toJSON([
                status: 'OK',
                event : [
                        project   : event.project,
                        branch    : event.branch,
                        type      : event.type,
                        parameters: event.parameters
                ],
        ]).write(rsp.writer)
    }

    protected static int getHttpCodeForEvent(@SuppressWarnings("UnusedParameters") SeedEvent event) {
        return StaplerResponse.SC_ACCEPTED;
    }

    protected static void sendError(StaplerResponse rsp, int httpCode, String message) throws IOException {
        rsp.setStatus(httpCode);
        rsp.setContentType("application/json");
        JSONSerializer.toJSON([
                status : 'ERROR',
                message: message,
        ]).write(rsp.writer)
    }

    protected void post(SeedEvent event) {
        seedService.post(event);
    }

}

package net.nemerosa.seed.triggering.connector.bitbucket;

import hudson.Extension;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.connector.AbstractEndPoint;
import net.nemerosa.seed.triggering.connector.UnknownRequestException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

@Extension
public class BitBucketEndPoint extends AbstractEndPoint {

    public static final String X_EVENT_KEY = "X-Event-Key";
    public static final String X_EVENT_VALUE = "repo:push";

    public BitBucketEndPoint(SeedService seedService) {
        super(seedService);
    }

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
        // FIXME Method net.nemerosa.seed.triggering.connector.bitbucket.BitBucketEndPoint.extractEvent
        throw new UnknownRequestException("Unknown request");
    }

    @Override
    public String getUrlName() {
        return "seed-bitbucket";
    }
}

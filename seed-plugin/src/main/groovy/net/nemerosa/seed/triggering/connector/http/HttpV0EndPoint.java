package net.nemerosa.seed.triggering.connector.http;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.connector.http.HttpEndPoint;

@Extension
@Deprecated
public class HttpV0EndPoint extends HttpEndPoint implements UnprotectedRootAction {

    public HttpV0EndPoint(SeedService seedService) {
        super(seedService);
    }

    public HttpV0EndPoint() {
        super(true);
    }

    @Override
    public String getUrlName() {
        return "seed-http";
    }
}

package net.nemerosa.seed.triggering.connector.http;

import net.nemerosa.jenkins.seed.triggering.connector.http.HttpEndPoint;
import net.nemerosa.jenkins.seed.triggering.connector.http.HttpEndPointTest;

public class HttpV0EndPointTest extends HttpEndPointTest {

    @Override
    public HttpEndPoint getEndPoint() {
        return new HttpV0EndPoint(seedService);
    }

}

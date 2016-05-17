package net.nemerosa.seed.triggering.connector.bitbucket;

import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.connector.bitbucket.BitBucketEndPoint;
import net.nemerosa.jenkins.seed.triggering.connector.bitbucket.BitBucketEndPointTest;

public class BitBucketV0EndPointTest extends BitBucketEndPointTest {

    @Override
    protected BitBucketEndPoint getEndPoint(SeedService seedService) {
        return new BitBucketV0EndPoint(seedService);
    }

}

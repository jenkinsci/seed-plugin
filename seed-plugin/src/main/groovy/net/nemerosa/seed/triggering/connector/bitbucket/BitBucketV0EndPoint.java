package net.nemerosa.seed.triggering.connector.bitbucket;

import hudson.Extension;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.connector.bitbucket.BitBucketEndPoint;

@Extension
@Deprecated
public class BitBucketV0EndPoint extends BitBucketEndPoint {

    BitBucketV0EndPoint(SeedService seedService) {
        super(seedService);
    }

    @SuppressWarnings("unused")
    public BitBucketV0EndPoint() {
        super(true);
    }

    @Override
    public String getUrlName() {
        return "seed-bitbucket";
    }

}

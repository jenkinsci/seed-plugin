package net.nemerosa.seed.triggering.connector.github;

import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.connector.github.GitHubEndPoint;
import net.nemerosa.jenkins.seed.triggering.connector.github.GitHubEndPointTest;

public class GitHubV0EndPointTest extends GitHubEndPointTest {

    @Override
    protected GitHubEndPoint getEndPoints(SeedService seedService) {
        return new GitHubV0EndPoint(seedService);
    }

}

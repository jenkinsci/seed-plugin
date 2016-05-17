package net.nemerosa.seed.triggering.connector.github;

import hudson.Extension;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.connector.github.GitHubEndPoint;

@Extension
@Deprecated
public class GitHubV0EndPoint extends GitHubEndPoint {

    public GitHubV0EndPoint(SeedService seedService) {
        super(seedService);
    }

    public GitHubV0EndPoint() {
        super(true);
    }

    @Override
    public String getUrlName() {
        return "seed-github";
    }

}

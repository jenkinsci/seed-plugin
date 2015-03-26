package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

@Extension
public class GitHubEndPoint extends AbstractEndPoint {

    private static final Logger LOGGER = Logger.getLogger(GitHubEndPoint.class.getName());

    public GitHubEndPoint(SeedService seedService) {
        super(seedService);
    }

    public GitHubEndPoint() {
        super();
    }

    @Override
    public String getUrlName() {
        return "seed-github";
    }

    @Override
    protected SeedEvent extractEvent(StaplerRequest req) {
        // Gets the event type sent by GitHub
        String ghEvent = req.getHeader("X-GitHub-Event");
        LOGGER.finer("GitHub event: " + ghEvent);
        // FIXME Method net.nemerosa.seed.jenkins.connector.github.GitHubEndPoint.extractEvent
        throw new RuntimeException("NYI");
    }
}

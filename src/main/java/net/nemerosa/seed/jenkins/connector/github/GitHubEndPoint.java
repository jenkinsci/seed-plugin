package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.AbstractEndPoint;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
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
    protected SeedEvent extractEvent(StaplerRequest req) throws IOException {
        // Gets the event type sent by GitHub
        String ghEvent = req.getHeader("X-GitHub-Event");
        // Reads the content as text
        String payload = IOUtils.toString(req.getReader());
        LOGGER.finer("GitHub event: " + ghEvent);
        // FIXME Method net.nemerosa.seed.jenkins.connector.github.GitHubEndPoint.extractEvent
        throw new RuntimeException("NYI");
    }
}

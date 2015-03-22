package net.nemerosa.seed.jenkins.connector.github;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.util.logging.Logger;

@Extension
public class SeedGitHubHook implements UnprotectedRootAction {

    private static final Logger LOGGER = Logger.getLogger(SeedGitHubHook.class.getName());
    public static final String URLNAME = "seed-github-hook";

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return URLNAME;
    }

    @RequirePOST
    public void doIndex(StaplerRequest req, StaplerResponse rsp) {
        LOGGER.info("Incoming POST");
        // Headers
        String xHubSignature = req.getHeader("X-Hub-Signature");
    }
}

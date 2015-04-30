package net.nemerosa.seed.config;

import hudson.Extension;
import hudson.model.RootAction;
import jenkins.model.GlobalConfiguration;
import net.nemerosa.seed.SeedPlugin;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;
import java.util.logging.Logger;

@Extension
public class SeedConfigurationEndPoint implements RootAction {

    private final Logger logger = Logger.getLogger(SeedConfigurationEndPoint.class.getName());

    @RequirePOST
    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        // Gets the payload
        String payload = IOUtils.toString(req.getReader());
        // Parsing for test
        SeedConfiguration.parseYaml(payload);
        // If OK, going on with the configuration
        SeedPlugin seedPlugin = GlobalConfiguration.all().get(SeedPlugin.class);
        seedPlugin.setSeedConfigurationContent(payload);
        seedPlugin.save();
        // OK
        logger.info("Seed configuration updated.");
    }

    public void doGet(StaplerRequest req, StaplerResponse rsp) throws IOException {
        SeedPlugin seedPlugin = GlobalConfiguration.all().get(SeedPlugin.class);
        rsp.setContentType("text/yaml");
        rsp.getWriter().write(seedPlugin.getSeedConfigurationContent());
    }

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
        return "seed-config";
    }
}

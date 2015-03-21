package net.nemerosa.seed.jenkins;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Configuration of the Seed plug-in.
 */
@Extension
public class SeedConfiguration extends GlobalConfiguration {

    public static SeedConfiguration getSeedConfiguration() {
        return (SeedConfiguration) Jenkins.getInstance().getDescriptor(SeedConfiguration.class);
    }

    private String seedConfigurationUrl;
    private String seedConfigurationContent;

    public SeedConfiguration() {
        load();
    }

    public String getSeedConfigurationUrl() {
        return seedConfigurationUrl;
    }

    public String getSeedConfigurationContent() {
        return seedConfigurationContent;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        seedConfigurationUrl = json.getString("seedConfigurationUrl");
        seedConfigurationContent = json.getString("seedConfigurationContent");
        save();
        return super.configure(req, json);
    }

}

package net.nemerosa.jenkins.seed;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Configuration of the Seed plug-in.
 */
@Extension
public class SeedPlugin extends GlobalConfiguration {

    public static SeedPlugin getSeedPlugin() {
        return GlobalConfiguration.all().get(SeedPlugin.class);
    }

    public SeedPlugin() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        save();
        return super.configure(req, json);
    }

}

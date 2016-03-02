package net.nemerosa.seed;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.nemerosa.seed.config.CannotReadConfigurationException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Configuration of the Seed plug-in.
 *
 * @deprecated Use v1 instead
 */
@Extension
@Deprecated
public class SeedPlugin extends GlobalConfiguration {

    public static SeedPlugin getSeedPlugin() {
        return GlobalConfiguration.all().get(SeedPlugin.class);
    }

    private String seedConfigurationUrl;
    private String seedConfigurationContent;

    public SeedPlugin() {
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

    public void setSeedConfigurationContent(String content) {
        seedConfigurationContent = content;
    }

    public String getYaml() {
        if (StringUtils.isNotBlank(seedConfigurationContent)) {
            return seedConfigurationContent;
        } else if (StringUtils.isNotBlank(seedConfigurationUrl)) {
            try {
                URL url = new URL(seedConfigurationUrl);
                InputStream in = url.openStream();
                try {
                    return IOUtils.toString(in, "UTF-8");
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new CannotReadConfigurationException(seedConfigurationUrl, ex);
            }
        } else {
            return "";
        }
    }
}

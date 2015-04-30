package net.nemerosa.seed.config;

import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedPlugin;
import net.nemerosa.seed.config.SeedConfiguration;

public class JenkinsSeedConfigurationLoader implements SeedConfigurationLoader {
    @Override
    public SeedConfiguration load() {
        // Gets the plugin's configuration
        SeedPlugin plugin = SeedPlugin.getSeedPlugin();
        // Gets the content
        String yaml = plugin.getYaml();
        // Parses the configuration
        return SeedConfiguration.parseYaml(yaml);
    }
}

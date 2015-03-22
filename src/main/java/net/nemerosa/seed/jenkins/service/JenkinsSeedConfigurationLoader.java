package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedPlugin;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;

import java.util.Collections;

public class JenkinsSeedConfigurationLoader implements SeedConfigurationLoader {
    @Override
    public SeedConfiguration load() {
        // Gets the plugin's configuration
        SeedPlugin plugin = SeedPlugin.getSeedPlugin();
        // Gets the content
        String yaml = plugin.getYaml();
        // TODO Parses the YAML
        // TODO Parses the configuration
        // FIXME Method net.nemerosa.seed.jenkins.service.JenkinsSeedConfigurationLoader.load
        return new SeedConfiguration(Collections.<SeedProjectConfiguration>emptyList());
    }
}

package net.nemerosa.seed.jenkins.test;

import net.nemerosa.seed.config.SeedConfigurationLoader;
import net.nemerosa.seed.config.SeedConfiguration;

public class MockSeedConfigurationLoader implements SeedConfigurationLoader {

    private final String yaml;

    public MockSeedConfigurationLoader() {
        this("");
    }

    public MockSeedConfigurationLoader(String yaml) {
        this.yaml = yaml;
    }

    @Override
    public SeedConfiguration load() {
        return SeedConfiguration.parseYaml(yaml);
    }
}

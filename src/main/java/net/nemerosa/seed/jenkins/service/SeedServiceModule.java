package net.nemerosa.seed.jenkins.service;

import com.google.inject.AbstractModule;
import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;

public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedConfigurationLoader.class).to(JenkinsSeedConfigurationLoader.class);
        bind(SeedLauncher.class).to(JenkinsSeedLauncher.class);
        bind(BranchStrategies.class).to(JenkinsBranchStrategies.class);
        bind(SeedService.class).to(SeedServiceImpl.class);
    }
}

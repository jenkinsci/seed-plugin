package net.nemerosa.seed.jenkins.service;

import com.google.inject.AbstractModule;
import net.nemerosa.seed.jenkins.SeedConfiguration;
import net.nemerosa.seed.jenkins.SeedService;

public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedConfiguration.class).to(JenkinsSeedConfiguration.class);
        bind(SeedService.class).to(SeedServiceImpl.class);
    }
}

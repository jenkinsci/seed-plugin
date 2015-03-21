package net.nemerosa.seed.jenkins.service;

import com.google.inject.AbstractModule;
import net.nemerosa.seed.jenkins.SeedService;

public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedService.class).to(SeedServiceImpl.class);
    }
}

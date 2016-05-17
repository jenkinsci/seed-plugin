package net.nemerosa.seed.triggering;

import com.google.inject.AbstractModule;
import net.nemerosa.jenkins.seed.triggering.JenkinsSeedLauncher;
import net.nemerosa.jenkins.seed.triggering.SeedLauncher;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.seed.config.*;

@Deprecated
public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedConfigurationLoader.class).to(JenkinsSeedConfigurationLoader.class);
        bind(SeedLauncher.class).to(JenkinsSeedLauncher.class);
        bind(BranchStrategies.class).to(JenkinsBranchStrategies.class);
        bind(SeedService.class).to(SeedServiceImpl.class);
        bind(SeedProjectConfigurationCache.class).to(JenkinsSeedProjectConfigurationCache.class);
    }
}

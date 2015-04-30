package net.nemerosa.seed.triggering;

import com.google.inject.AbstractModule;
import net.nemerosa.seed.config.SeedConfigurationLoader;
import net.nemerosa.seed.config.JenkinsBranchStrategies;
import net.nemerosa.seed.config.JenkinsSeedConfigurationLoader;
import net.nemerosa.seed.config.BranchStrategies;

public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedConfigurationLoader.class).to(JenkinsSeedConfigurationLoader.class);
        bind(SeedLauncher.class).to(JenkinsSeedLauncher.class);
        bind(BranchStrategies.class).to(JenkinsBranchStrategies.class);
        bind(SeedService.class).to(SeedServiceImpl.class);
    }
}

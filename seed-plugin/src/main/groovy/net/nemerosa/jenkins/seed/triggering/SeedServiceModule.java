package net.nemerosa.jenkins.seed.triggering;

import com.google.inject.AbstractModule;
import net.nemerosa.jenkins.seed.cache.ProjectSeedCache;
import net.nemerosa.jenkins.seed.cache.ProjectSeedCacheImpl;

public class SeedServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeedLauncher.class).to(JenkinsSeedLauncher.class);
        bind(SeedService.class).to(SeedServiceImpl.class);
        bind(ProjectSeedCache.class).to(ProjectSeedCacheImpl.class);
    }

}

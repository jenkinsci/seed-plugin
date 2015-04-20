package net.nemerosa.seed.jenkins.support;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;

public class SeedProjectHelper {

    private final SeedConfiguration globalConfiguration;
    private final SeedProjectConfiguration projectConfiguration;
    private final BranchStrategy branchStrategy;
    private final SeedNamingStrategy namingStrategy;

    public SeedProjectHelper(SeedConfiguration globalConfiguration, SeedProjectConfiguration projectConfiguration, BranchStrategy branchStrategy, SeedNamingStrategy namingStrategy) {
        this.globalConfiguration = globalConfiguration;
        this.projectConfiguration = projectConfiguration;
        this.branchStrategy = branchStrategy;
        this.namingStrategy = namingStrategy;
    }

    public SeedConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    public SeedProjectConfiguration getProjectConfiguration() {
        return projectConfiguration;
    }

    public BranchStrategy getBranchStrategy() {
        return branchStrategy;
    }

    public SeedNamingStrategy getNamingStrategy() {
        return namingStrategy;
    }
}

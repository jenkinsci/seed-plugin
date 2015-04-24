package net.nemerosa.seed.jenkins.support;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;
import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper;

public class SeedProjectEnvironment {

    private final String id;
    private final String scmType;
    private final String scmUrl;
    private final SeedConfiguration globalConfiguration;
    private final SeedProjectConfiguration projectConfiguration;
    private final BranchStrategy branchStrategy;
    private final SeedNamingStrategy namingStrategy;

    public SeedProjectEnvironment(String id, String scmType, String scmUrl, SeedConfiguration globalConfiguration, SeedProjectConfiguration projectConfiguration, BranchStrategy branchStrategy, SeedNamingStrategy namingStrategy) {
        this.id = id;
        this.scmType = scmType;
        this.scmUrl = scmUrl;
        this.globalConfiguration = globalConfiguration;
        this.projectConfiguration = projectConfiguration;
        this.branchStrategy = branchStrategy;
        this.namingStrategy = namingStrategy;
    }

    public String getId() {
        return id;
    }

    public String getScmType() {
        return scmType;
    }

    public String getScmUrl() {
        return scmUrl;
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

    public String getProjectSeedFolder() {
        return SeedNamingStrategyHelper.getProjectSeedFolder(namingStrategy, id);
    }

    public String getProjectSeed() {
        return namingStrategy.getProjectSeed(id);
    }
}

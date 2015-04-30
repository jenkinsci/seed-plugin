package net.nemerosa.seed.config;

import java.util.List;

public class SeedProjectEnvironment {

    private final String id;
    private final String projectClass;
    private final String scmType;
    private final String scmUrl;
    private final SeedConfiguration globalConfiguration;
    private final SeedProjectConfiguration projectConfiguration;
    private final BranchStrategy branchStrategy;
    private final SeedNamingStrategy namingStrategy;

    public SeedProjectEnvironment(String id, String projectClass, String scmType, String scmUrl, SeedConfiguration globalConfiguration, SeedProjectConfiguration projectConfiguration, BranchStrategy branchStrategy, SeedNamingStrategy namingStrategy) {
        this.id = id;
        this.projectClass = projectClass;
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

    public String getProjectClass() {
        return projectClass;
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

    public String getConfigurationValue(String key, String defaultValue) {
        return Configuration.getValue(
                key,
                projectConfiguration,
                globalConfiguration,
                defaultValue
        );
    }

    public List<String> getConfigurationList(String key) {
        return Configuration.getList(
                key,
                projectConfiguration,
                globalConfiguration
        );
    }
}

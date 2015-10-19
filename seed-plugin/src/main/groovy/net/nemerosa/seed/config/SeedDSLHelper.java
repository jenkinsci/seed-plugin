package net.nemerosa.seed.config;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class SeedDSLHelper {

    private final SeedConfigurationLoader configurationLoader;
    private final SeedProjectConfigurationCache projectConfigurationCache;
    private final BranchStrategies branchStrategies;

    public SeedDSLHelper() {
        this(
                new JenkinsSeedConfigurationLoader(),
                new JenkinsSeedProjectConfigurationCache(),
                new JenkinsBranchStrategies()
        );
    }

    public SeedDSLHelper(SeedConfigurationLoader configurationLoader, SeedProjectConfigurationCache projectConfigurationCache, BranchStrategies branchStrategies) {
        this.configurationLoader = configurationLoader;
        this.projectConfigurationCache = projectConfigurationCache;
        this.branchStrategies = branchStrategies;
    }

    /**
     * Gets the configuration for a project
     */
    public SeedProjectEnvironment getProjectEnvironment(
            String project, String projectClass, String scmType, String scmUrl, String scmCredentials,
            boolean useCache) {
        if (useCache) {
            SeedProjectSavedConfiguration cache = projectConfigurationCache.load(project);
            if (cache != null && cache.sameAs(projectClass, scmType, scmUrl, scmCredentials)) {
                SeedConfiguration globalConfiguration = new SeedConfiguration(cache.getGlobalConfiguration());
                SeedProjectConfiguration projectConfiguration = new SeedProjectConfiguration(cache.getProjectConfiguration());
                return getProjectEnvironment(
                        project, projectClass, scmType, scmUrl, scmCredentials,
                        globalConfiguration,
                        projectConfiguration
                );
            }
        }
        // No cache
        SeedConfiguration globalConfiguration = configurationLoader.load();
        SeedProjectConfiguration projectConfiguration = globalConfiguration.getProjectConfiguration(project, projectClass);
        return getProjectEnvironment(
                project, projectClass, scmType, scmUrl, scmCredentials,
                globalConfiguration,
                projectConfiguration
        );
    }

    private SeedProjectEnvironment getProjectEnvironment(String project, String projectClass, String scmType, String scmUrl, String scmCredentials, SeedConfiguration globalConfiguration, SeedProjectConfiguration projectConfiguration) {
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(globalConfiguration, projectConfiguration, branchStrategies);
        SeedNamingStrategy namingStrategy = branchStrategy.getSeedNamingStrategy();
        return new SeedProjectEnvironment(
                project, projectClass, scmType, scmUrl, scmCredentials,
                globalConfiguration,
                projectConfiguration,
                branchStrategy,
                namingStrategy
        );
    }

    /**
     * Gets the text content of a resource
     */
    public static String getResourceAsText(String path) throws IOException {
        return IOUtils.toString(SeedDSLHelper.class.getResource(path));
    }

}

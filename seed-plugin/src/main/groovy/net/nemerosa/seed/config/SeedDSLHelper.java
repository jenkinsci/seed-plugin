package net.nemerosa.seed.config;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class SeedDSLHelper {

    private final SeedConfigurationLoader configurationLoader;
    private final BranchStrategies branchStrategies;

    public SeedDSLHelper() {
        this(
                new JenkinsSeedConfigurationLoader(),
                new JenkinsBranchStrategies()
        );
    }

    public SeedDSLHelper(SeedConfigurationLoader configurationLoader, BranchStrategies branchStrategies) {
        this.configurationLoader = configurationLoader;
        this.branchStrategies = branchStrategies;
    }

    /**
     * Gets the configuration for a project
     *
     * @deprecated Must be used only by the {@link net.nemerosa.seed.generator.ProjectSeedBuilder}
     */
    @Deprecated
    public SeedProjectEnvironment getProjectEnvironment(String project, String projectClass, String scmType, String scmUrl, String scmCredentials) {
        SeedConfiguration configuration = configurationLoader.load();
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(project, projectClass);
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(configuration, projectConfiguration, branchStrategies);
        SeedNamingStrategy namingStrategy = branchStrategy.getSeedNamingStrategy();
        return new SeedProjectEnvironment(
                project, projectClass, scmType, scmUrl, scmCredentials,
                configuration,
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

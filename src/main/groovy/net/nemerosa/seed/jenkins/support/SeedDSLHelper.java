package net.nemerosa.seed.jenkins.support;

import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.service.JenkinsBranchStrategies;
import net.nemerosa.seed.jenkins.service.JenkinsSeedConfigurationLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.BranchStrategyHelper;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;
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
     */
    public SeedProjectEnvironment getProjectEnvironment(String project, String projectClass, String scmType, String scmUrl) {
        SeedConfiguration configuration = configurationLoader.load();
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(project, projectClass);
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(configuration, projectConfiguration, branchStrategies);
        SeedNamingStrategy namingStrategy = branchStrategy.getSeedNamingStrategy();
        return new SeedProjectEnvironment(
                project, projectClass, scmType, scmUrl,
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

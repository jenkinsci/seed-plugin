package net.nemerosa.seed.jenkins.support;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.service.JenkinsBranchStrategies;
import net.nemerosa.seed.jenkins.service.JenkinsSeedConfigurationLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.BranchStrategyHelper;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public final class SeedDSLHelper {

    private SeedDSLHelper() {
    }

    private static final JenkinsSeedConfigurationLoader configurationLoader = new JenkinsSeedConfigurationLoader();
    private static final JenkinsBranchStrategies branchStrategies = new JenkinsBranchStrategies();

    /**
     * Gets the configuration for a project
     */
    public static SeedProjectHelper getProjectHelper(String project, String projectClass) {
        SeedConfiguration configuration = configurationLoader.load();
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(project, projectClass);
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(configuration, projectConfiguration, branchStrategies);
        SeedNamingStrategy namingStrategy = branchStrategy.getSeedNamingStrategy();
        return new SeedProjectHelper(
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

package net.nemerosa.seed.jenkins.support;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.service.JenkinsBranchStrategies;
import net.nemerosa.seed.jenkins.service.JenkinsSeedConfigurationLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.BranchStrategyHelper;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;

public final class SeedDSLHelper {

    private SeedDSLHelper() {
    }

    private static final JenkinsSeedConfigurationLoader configurationLoader = new JenkinsSeedConfigurationLoader();
    private static final JenkinsBranchStrategies branchStrategies = new JenkinsBranchStrategies();

    /**
     * Gets the naming strategy for a project
     */
    public static SeedNamingStrategy getSeedNamingStrategy(String project) {
        SeedConfiguration configuration = configurationLoader.load();
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(project);
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(configuration, projectConfiguration, branchStrategies);
        return branchStrategy.getSeedNamingStrategy();
    }

}

import net.nemerosa.seed.jenkins.model.SeedConfiguration
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration
import net.nemerosa.seed.jenkins.service.JenkinsBranchStrategies
import net.nemerosa.seed.jenkins.service.JenkinsSeedConfigurationLoader
import net.nemerosa.seed.jenkins.strategy.BranchStrategy
import net.nemerosa.seed.jenkins.strategy.BranchStrategyHelper
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy
import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper

/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 */

// TODO Use a supporting class

SeedConfiguration configuration = new JenkinsSeedConfigurationLoader().load()
SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(PROJECT)
BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(configuration, projectConfiguration, new JenkinsBranchStrategies())
SeedNamingStrategy namingStrategy = branchStrategy.seedNamingStrategy

folder(SeedNamingStrategyHelper.getProjectSeedFolder(namingStrategy, PROJECT as String)) {
    // TODO Authorisations for the project, part of the project configuration
}

freeStyleJob(namingStrategy.getProjectSeed(PROJECT as String)) {
    description "Project seed for ${PROJECT}"
}

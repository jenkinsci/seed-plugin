import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper
import net.nemerosa.seed.jenkins.support.SeedDSLHelper

/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 */

def namingStrategy = SeedDSLHelper.getSeedNamingStrategy(PROJECT as String)

folder(SeedNamingStrategyHelper.getProjectSeedFolder(namingStrategy, PROJECT as String)) {
    // TODO Authorisations for the project, part of the project configuration
}

freeStyleJob(namingStrategy.getProjectSeed(PROJECT as String)) {
    description "Project seed for ${PROJECT}"
}

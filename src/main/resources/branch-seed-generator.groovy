import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper
import net.nemerosa.seed.jenkins.support.SeedDSLHelper

/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 * - PROJECT_SCM_TYPE
 * - PROJECT_SCM_URL
 * - BRANCH
 * - TODO SCM parameters for the branch
 */

def namingStrategy = SeedDSLHelper.getSeedNamingStrategy(PROJECT as String)

/**
 * Branch folder
 */

folder(SeedNamingStrategyHelper.getBranchSeedFolder(namingStrategy, PROJECT as String, BRANCH as String)) {}
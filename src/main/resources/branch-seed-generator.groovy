import net.nemerosa.seed.jenkins.scm.SCMHelper
import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper
import net.nemerosa.seed.jenkins.support.SeedDSLHelper

/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 * - PROJECT_CLASS - Class of project
 * - PROJECT_SCM_TYPE
 * - PROJECT_SCM_URL
 * - BRANCH
 */

def projectHelper = SeedDSLHelper.getProjectHelper(PROJECT as String, PROJECT_CLASS as String)

/**
 * Branch folder
 */

folder(SeedNamingStrategyHelper.getBranchSeedFolder(projectHelper.namingStrategy, PROJECT as String, BRANCH as String)) {}

/**
 * Branch seed
 */

freeStyleJob(projectHelper.namingStrategy.getBranchSeed(PROJECT as String, BRANCH as String)) {
    description "Branch seed for ${BRANCH} in ${PROJECT} - generates the pipeline for the ${BRANCH} branch."
    scm {
        SCMHelper.downloadPartial(delegate, projectHelper.projectConfiguration, PROJECT_SCM_TYPE as String, PROJECT_SCM_URL as String, BRANCH as String, 'seed')
    }
}

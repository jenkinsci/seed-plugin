import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper

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
 *
 * Bound variables are:
 *
 * - seedDSLHelper
 */

def projectEnvironment = seedDSLHelper.getProjectEnvironment(
        PROJECT as String, PROJECT_CLASS as String,
        PROJECT_SCM_TYPE as String, PROJECT_SCM_URL as String)

/**
 * Branch folder
 */

folder(SeedNamingStrategyHelper.getBranchSeedFolder(projectEnvironment.namingStrategy, PROJECT as String, BRANCH as String)) {}

/**
 * Branch seed
 */

freeStyleJob(projectEnvironment.namingStrategy.getBranchSeed(PROJECT as String, BRANCH as String)) {
    description "Branch seed for ${BRANCH} in ${PROJECT} - generates the pipeline for the ${BRANCH} branch."
//    wrappers {
//        environmentVariables {
//            env('PROJECT', PROJECT)
//            env('PROJECT_CLASS', PROJECT_CLASS)
//            env('PROJECT_SCM_TYPE', PROJECT_SCM_TYPE)
//            env('PROJECT_SCM_URL', PROJECT_SCM_URL)
//            env('BRANCH', BRANCH)
//        }
//    }
//    scm {
//        SCMHelper.downloadPartial(delegate, projectEnvironment.projectConfiguration, PROJECT_SCM_TYPE as String, PROJECT_SCM_URL as String, BRANCH as String, 'seed')
//    }
//    PipelineHelper.pipelineGenerationSteps delegate, projectEnvironment
}

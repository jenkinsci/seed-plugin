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
 * - SEED_PROJECT
 * - SEED_BRANCH
 *
 * Bound variables are:
 *
 * - branchSeedFolder
 * - branchSeedPath
 *
 * Extension points are:
 *
 * - branchSeedScm
 * - pipelineGeneration
 */

/**
 * Branch folder
 */

folder(branchSeedFolder) {}

/**
 * Branch seed
 */

freeStyleJob(branchSeedPath) {
    description "Branch seed for ${BRANCH} in ${PROJECT} - generates the pipeline for the ${BRANCH} branch."
    // TODO branchSeedScmEnvExtensionPoint()
    wrappers {
        environmentVariables {
            env('PROJECT', PROJECT)
            env('PROJECT_CLASS', PROJECT_CLASS)
            env('PROJECT_SCM_TYPE', PROJECT_SCM_TYPE)
            env('PROJECT_SCM_URL', PROJECT_SCM_URL)
            env('PROJECT_SCM_CREDENTIALS', PROJECT_SCM_CREDENTIALS)
            env('BRANCH', BRANCH)
            env('SEED_PROJECT', SEED_PROJECT)
            env('SEED_BRANCH', SEED_BRANCH)
            // Additional parameters for the branch
            branchSeedBranchParametersExtensionPoint()
        }
    }
    branchSeedScmExtensionPoint()
    pipelineGenerationExtensionPoint()
}

/**
 * Firing the branch seed
 */

queue(branchSeedPath)

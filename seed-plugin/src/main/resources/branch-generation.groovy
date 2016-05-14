/**
 * Script to generate a branch folder and seed.
 *
 * @see net.nemerosa.jenkins.seed.generator.BranchGenerationStep
 */

/**
 * Branch folder
 */

folder(BRANCH_FOLDER_PATH) {}

/**
 * Branch seed
 */

job("${BRANCH_FOLDER_PATH}/${BRANCH_SEED_NAME}") {
    description "Branch seed for ${BRANCH} in ${PROJECT} - generates the pipeline for the ${BRANCH} branch."
    // TODO PipelineGenerationStep
    // TODO branchSeedScmExtensionPoint()
    // TODO pipelineGenerationExtensionPoint()
}

/**
 * Firing the branch seed
 */

if (EVENT_STRATEGY_AUTO == "yes") {
    queue("${BRANCH_FOLDER_PATH}/${BRANCH_SEED_NAME}")
}

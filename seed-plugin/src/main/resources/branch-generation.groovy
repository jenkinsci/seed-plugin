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
    // Pipeline generation
    configure { node ->
        node / 'builders' / 'net.nemerosa.jenkins.seed.generator.PipelineGenerationStep' {
            // The whole pipeline configuration is no longer needed, only project parameters
            // and branch parameters
            // Project
            project PROJECT
            scmType PROJECT_SCM_TYPE
            scmUrl PROJECT_SCM_URL
            scmCredentials PROJECT_SCM_CREDENTIALS
            // Branch
            branch BRANCH
            // TODO Branch parameters (extension)
            // Jenkins-safe names
            branchSeedName BRANCH_SEED_NAME
        }
    }
    // SCM configuration
    branchSeedScmExtensionPoint()
    // TODO pipelineGenerationExtensionPoint()
}

/**
 * Firing the branch seed
 */

if (EVENT_STRATEGY_AUTO == "yes") {
    queue("${BRANCH_FOLDER_PATH}/${BRANCH_SEED_NAME}")
}

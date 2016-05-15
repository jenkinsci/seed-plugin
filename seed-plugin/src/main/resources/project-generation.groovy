/**
 * Script to generate a project folder and seed.
 *
 * @see net.nemerosa.jenkins.seed.generator.ProjectGenerationStep
 */

folder(PROJECT_FOLDER_PATH) {
    projectAuthorisationsExtensionPoint()
}

job("${PROJECT_FOLDER_PATH}/${PROJECT_SEED_NAME}") {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        // Default seed parameters
        stringParam('BRANCH', '', 'Path or name of the branch')
    }
    steps {
        buildDescription('', '${BRANCH}')
    }
    // Branch generation
    configure { node ->
        node / 'builders' / 'net.nemerosa.jenkins.seed.generator.BranchGenerationStep' {
            projectConfig {
                pipelineConfig {
                    destructor PIPELINE_DESTRUCTOR
                    commitParameter PIPELINE_COMMIT_PARARAMETER
                    authorisations PIPELINE_AUTHORISATIONS
                    branchSCMParameter PIPELINE_BRANCH_SCM_PARAMETER
                    branchParameters PIPELINE_BRANCH_PARAMETERS
                    generationExtension PIPELINE_GENERATION_EXTENSION
                    namingStrategy {
                        projectFolderPath PROJECT_FOLDER_PATH
                        projectSeedName PROJECT_SEED_NAME
                        projectDestructorName PROJECT_DESTRUCTOR_NAME
                        branchFolderPath BRANCH_FOLDER_PATH
                        branchSeedName BRANCH_SEED_NAME
                        branchStartName BRANCH_START_NAME
                        branchName BRANCH_NAME
                    }
                    eventStrategy {
                        delete EVENT_STRATEGY_DELETE
                        auto EVENT_STRATEGY_AUTO
                        trigger EVENT_STRATEGY_TRIGGER
                        commit EVENT_STRATEGY_COMMIT
                        startAuto EVENT_STRATEGY_START_AUTO
                    }
                }
                project PROJECT
                scmType PROJECT_SCM_TYPE
                scmUrl PROJECT_SCM_URL
                scmCredentials PROJECT_SCM_CREDENTIALS
            }
        }
    }
    // Extension points
    projectGenerationExtensionPoint()
}

// Generates a destructor only if an option is defined for the project
println "PIPELINE_DESTRUCTOR = ${PIPELINE_DESTRUCTOR}"
if (PIPELINE_DESTRUCTOR == "true") {
    job("${PROJECT_FOLDER_PATH}/${PROJECT_DESTRUCTOR_NAME}") {
        description "Branch destructor for ${PROJECT} - deletes a branch folder."
        parameters {
            // Default seed parameters
            stringParam('BRANCH', '', 'Path or name of the branch')
        }
        steps {
            buildDescription('', '${BRANCH}')
        }
        // TODO BranchDestructionStep
//        configure { node ->
//            node / 'builders' / 'net.nemerosa.seed.generator.BranchDestructionBuilder' {
//                'project' PROJECT
//                'branch' '${BRANCH}'
//            }
//        }
    }
}
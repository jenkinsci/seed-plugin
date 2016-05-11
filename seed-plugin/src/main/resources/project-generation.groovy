/**
 * Script to generate a project folder and seed.
 *
 * @see net.nemerosa.jenkins.seed.generator.ProjectGenerationStep
 */

folder(PROJECT_SEED_FOLDER) {
    projectAuthorisationsExtensionPoint()
}

freeStyleJob("${PROJECT_SEED_FOLDER}/${PROJECT_SEED_JOB}") {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        // Default seed parameters
        stringParam('BRANCH', '', 'Path or name of the branch')
    }
    steps {
        buildDescription('', '${BRANCH}')
    }
    // TODO BranchGenerationStep
//    configure { node ->
//        node / 'builders' / 'net.nemerosa.seed.generator.BranchSeedBuilder' {
//            'project' PROJECT
//            'projectClass' PROJECT_CLASS
//            'projectScmType' PROJECT_SCM_TYPE
//            'projectScmUrl' PROJECT_SCM_URL
//            'projectScmCredentials' PROJECT_SCM_CREDENTIALS
//            'branch' '${BRANCH}'
//        }
//    }
    // TODO projectGenerationExtensionPoint()
}

// Generates a destructor only if an option is defined for the project
println "PROJECT_DESTRUCTOR_ENABLED = ${PROJECT_DESTRUCTOR_ENABLED}"
if (PROJECT_DESTRUCTOR_ENABLED == "true") {
    freeStyleJob("${PROJECT_SEED_FOLDER}/${PROJECT_DESTRUCTOR_PATH}") {
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
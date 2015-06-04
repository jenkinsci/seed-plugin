/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 * - PROJECT_CLASS - Class of project
 * - PROJECT_SCM_TYPE
 * - PROJECT_SCM_URL
 *
 * Bound variables are:
 *
 * - projectSeedFolder
 * - projectSeedPath
 *
 * Extension points are:
 *
 * - authorisations
 * - environment variables
 */

folder(projectSeedFolder) {
    authorisationsExtensionPoint()
}

freeStyleJob(projectSeedPath) {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        // Additional parameters for the branch
        projectSeedBranchParametersExtensionPoint()
        // Default seed parameters
        stringParam('BRANCH', '', 'Path or name of the branch')
    }
    steps {
        buildDescription('', '${BRANCH}')
    }
    configure { node ->
        node / 'builders' / 'net.nemerosa.seed.generator.BranchSeedBuilder' {
            'project' PROJECT
            'projectClass' PROJECT_CLASS
            'projectScmType' PROJECT_SCM_TYPE
            'projectScmUrl' PROJECT_SCM_URL
            'projectScmCredentials' PROJECT_SCM_CREDENTIALS
            'branch' '${BRANCH}'
        }
    }
}

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
 */

folder(projectSeedFolder) {
    authorisationsExtensionPoint()
}

freeStyleJob(projectSeedPath) {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        stringParam('BRANCH', '', 'Path to the branch')
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
            'branch' '${BRANCH}'
        }
    }
}

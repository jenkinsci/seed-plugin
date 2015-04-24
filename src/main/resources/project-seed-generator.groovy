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
 */

folder(projectSeedFolder) {
    // TODO Authorisations for the project, part of the project configuration
}

freeStyleJob(projectSeedPath) {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        stringParam('BRANCH', '', 'Path to the branch')
    }
    configure { node ->
        node / 'builders' / 'net.nemerosa.seed.jenkins.step.BranchSeedBuilder' {
            'project' PROJECT
            'projectClass' PROJECT_CLASS
            'projectScmType' PROJECT_SCM_TYPE
            'projectScmUrl' PROJECT_SCM_URL
            'branch' '${BRANCH}'
        }
    }
}

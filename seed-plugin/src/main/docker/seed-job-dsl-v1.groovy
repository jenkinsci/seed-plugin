job {
    name 'seed-v1'
    description '''\
This job is used to generate a project.
'''
    // Runs only on the master
    label 'master'
    // List of parameters
    parameters {
        // Standard Seed parameters
        stringParam('PROJECT', '', 'Name of the project to generate - used as an identifier in Coastguard')
        choiceParam('PROJECT_SCM_TYPE', ['svn', 'git'])
        stringParam('PROJECT_SCM_URL', '', 'URL to the project SCM location, without any branch location')
        stringParam('PROJECT_SCM_CREDENTIALS', '', 'UUID of the SCM credentials')
    }
    steps {
        buildDescription('', '${PROJECT}')
    }
    // TODO V1 configuration
    configure { node ->
        node / 'builders' / 'net.nemerosa.seed.generator.ProjectSeedBuilder' {
            'project' '${PROJECT}'
            'projectClass' '${PROJECT_CLASS}'
            'projectScmType' '${PROJECT_SCM_TYPE}'
            'projectScmUrl' '${PROJECT_SCM_URL}'
            'projectScmCredentials' '${PROJECT_SCM_CREDENTIALS}'
        }
    }
}
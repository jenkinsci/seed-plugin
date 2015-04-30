job {
    name 'seed'
    description '''\
This job is used to create a seed for a project.
'''
    // Runs only on the master
    label 'master'
    // List of parameters
    parameters {
        // Standard Seed parameters
        stringParam('PROJECT', '', 'Name of the project to generate - used as an identifier in Coastguard')
        stringParam('PROJECT_CLASS', '', 'Class of the project to generate - optional')
        choiceParam('PROJECT_SCM_TYPE', ['svn', 'git'])
        stringParam('PROJECT_SCM_URL', '', 'URL to the project SCM location, without any branch location')
    }
    configure { node ->
        node / 'builders' / 'net.nemerosa.seed.generator.ProjectSeedBuilder' {
            'project' '${PROJECT}'
            'projectClass' '${PROJECT_CLASS}'
            'projectScmType' '${PROJECT_SCM_TYPE}'
            'projectScmUrl' '${PROJECT_SCM_URL}'
        }
    }
}
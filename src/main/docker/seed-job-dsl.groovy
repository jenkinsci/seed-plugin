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
        choiceParam('PROJECT_SCM_TYPE', ['SVN', 'GIT'])
        stringParam('PROJECT_SCM_URL', '', 'URL to the project SCM location, without any branch location')
    }
    // TODO Adds the step
}
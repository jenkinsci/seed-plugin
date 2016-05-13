job {
    name 'seed-generator'
    description '''\
This job is used to run any arbitrary job DSL.
'''
    // Runs only on the master
    label 'master'
    // List of parameters
    parameters {
        fileParam 'dsl.groovy', 'Groovy DSL to run'
    }
    steps {
        // Runs the DSL file
        dsl {
            external 'dsl.groovy'
        }
    }
}

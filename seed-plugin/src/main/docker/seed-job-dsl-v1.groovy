job {
    name 'seed-generator'
    description '''\
This job is used to run any arbitrary job DSL.
'''
    // Runs only on the master
    label 'master'
    // List of parameters
    parameters {
        textParam 'DSL', '', 'Groovy DSL to run'
    }
    steps {
        // Inject the DSL code into a file
        shell 'echo $DSL > dsl.groovy'
        // Runs the DSL file
        dsl {
            external 'dsl.groovy'
        }
    }
}

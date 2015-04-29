import hudson.model.*
import jenkins.model.*

Thread.start {
    sleep 10000
    println "--> generating the seed job"
    def process = new ProcessBuilder(
            'java',
            '-jar',
            '/var/lib/jenkins/init.groovy.d/job-dsl-core-standalone.jar',
            '/var/lib/jenkins/dsl/seed-job-dsl.groovy'
    )
            .directory(new File('/var/lib/jenkins/dsl'))
            .redirectErrorStream(true)
            .start()
    int code = process.waitFor()
    if (code != 0) {
        def message = "Error while generating a job: [${code}]"
        println message
        println process.text
        throw new RuntimeException(message)
    }
    // Importing the XML as a new job
    println "--> creating the seed job"
    new File('/var/lib/jenkins/dsl/seed.xml').withInputStream {
        Jenkins.instance.createProjectFromXML('seed', it)
    }
}

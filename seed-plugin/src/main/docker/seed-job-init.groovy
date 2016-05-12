import jenkins.model.*

def generateJob(String dslFile, String jobName) {
    Thread.start {
        sleep 10000
        println "--> generating the ${jobName} job"
        def process = new ProcessBuilder(
                'java',
                '-jar',
                '/var/lib/jenkins/init.groovy.d/job-dsl-core-standalone.jar',
                "/var/lib/jenkins/dsl/${dslFile}"
        )
                .directory(new File("/var/lib/jenkins/dsl/${jobName}"))
                .redirectErrorStream(true)
                .start()
        int code = process.waitFor()
        if (code != 0) {
            def message = "Error while generating ${jobName}: [${code}]"
            println message
            println process.text
            throw new RuntimeException(message)
        }
        // Importing the XML as a new job
        println "--> creating the ${jobName} job"
        new File("/var/lib/jenkins/dsl/${jobName}/${jobName}.xml").withInputStream {
            Jenkins.instance.createProjectFromXML(jobName, it)
        }
    }

}

generateJob '/var/lib/jenkins/dsl/seed-job-dsl.groovy', 'seed'
generateJob '/var/lib/jenkins/dsl/seed-job-dsl-v1.groovy', 'seed-v1'

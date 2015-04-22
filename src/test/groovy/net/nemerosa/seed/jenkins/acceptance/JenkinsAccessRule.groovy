package net.nemerosa.seed.jenkins.acceptance

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class JenkinsAccessRule implements TestRule {

    URL jenkinsUrl
    int connectionTries

    @Override
    Statement apply(Statement base, Description description) {
        // Connection timeout (in seconds, defaults to 2 minutes)
        int jenkinsTimeoutMin = System.getProperty('jenkinsTimeout', '2') as int
        int jenkinsTimeout = jenkinsTimeoutMin * 60
        connectionTries = (jenkinsTimeout + 4) / 5
        // Jenkins end point
        jenkinsUrl = new URL(System.getProperty('jenkinsUrl', 'http://localhost:8080'))
        println """Running "${description.methodName}" against ${jenkinsUrl}"""
        // Runs the tests
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                // Waiting for the Jenkins instance to be available
                api('')
                // Runs the tests
                base.evaluate()
            }
        }
    }

    public void job(String path) {
        // TODO Replaces / by /job/
        api(path)
    }

    // TODO Parses the resulting JSON
    public void api(String path) {
        String prefix
        if (path.endsWith('/')) {
            prefix = path
        } else {
            prefix = path + '/'
        }
        def apiUrl = new URL(jenkinsUrl, "${prefix}api/json")
        boolean connectionOk = false
        int tries = 0
        println """Waiting for ${apiUrl} to be available (${connectionTries} tries every 5 seconds)..."""
        while (!connectionOk && tries < connectionTries) {
            tries++
            print "Try #${tries}..."
            HttpURLConnection connection = apiUrl.openConnection() as HttpURLConnection
            try {
                try {
                    connection.connect()
                    def code = connection.getResponseCode()
                    println "Code = ${code}"
                    connectionOk = (code == HttpURLConnection.HTTP_OK)
                    // TODO Parses the JSON
                    // Error codes
                    if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        throw new JenkinsAPINotFoundException(apiUrl)
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (SocketException ignored) {
                // Trying again...
                println "Cannot connect"
            }
            if (!connectionOk) {
                sleep 5000
            }
        }
        // If connection is still not OK, that's a failure
        if (!connectionOk) {
            throw new JenkinsAPINotAvailableException(apiUrl)
        }
    }

}

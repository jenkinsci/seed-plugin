package net.nemerosa.seed.jenkins.docker

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import static org.junit.Assert.fail

class JenkinsAccessRule implements TestRule {

    URL jenkinsUrl

    @Override
    Statement apply(Statement base, Description description) {
        // Connection timeout (in seconds, defaults to 2 minutes)
        int jenkinsTimeoutMin = System.getProperty('jenkinsTimeout', '2') as int
        int jenkinsTimeout = jenkinsTimeoutMin * 60
        int connectionTries = (jenkinsTimeout + 9) / 10
        // Jenkins end point
        jenkinsUrl = new URL(System.getProperty('jenkinsUrl', 'http://localhost:8080'))
        println """Running "${description.methodName}" against ${jenkinsUrl}"""
        // Runs the tests
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                def apiUrl = new URL(jenkinsUrl, "api/json")
                // Waiting for the Jenkins instance to be available
                println """Waiting for ${apiUrl} to be available after ${jenkinsTimeoutMin} minutes (${
                    connectionTries
                } tries every 10 seconds)..."""
                boolean connectionOk = false
                int tries = 0
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
                        } finally {
                            connection.disconnect()
                        }
                    } catch (SocketException ignored) {
                        // Trying again...
                        println "Cannot connect"
                    }
                    if (!connectionOk) {
                        sleep 10000
                    }
                }
                // If connection is still not OK, that's a failure
                if (!connectionOk) {
                    fail "Connection to ${jenkinsUrl} could not be established after ${jenkinsTimeoutMin} minutes."
                }
                // Runs the tests
                base.evaluate()
            }
        }
    }

}

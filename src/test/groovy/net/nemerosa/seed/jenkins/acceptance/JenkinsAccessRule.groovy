package net.nemerosa.seed.jenkins.acceptance

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class JenkinsAccessRule implements TestRule {

    /**
     * Retry interval in milliseconds
     */
    static final int INTERVAL = 5 * 1000L

    URL jenkinsUrl

    @Override
    Statement apply(Statement base, Description description) {
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

    public void job(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        // TODO Replaces / by /job/
        api("job/${path}", timeoutSeconds, timeoutOnNotFound)
    }

    // TODO Parses the resulting JSON
    public void api(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        String prefix
        if (path.endsWith('/')) {
            prefix = path
        } else {
            prefix = path + '/'
        }
        def apiUrl = new URL(jenkinsUrl, "${prefix}api/json")
        boolean connectionOk = false
        int tries = 0
        int durationSeconds = 0
        if (timeoutOnNotFound && timeoutOnNotFound != timeoutSeconds) {
            println """Waiting for ${apiUrl} to be available in ${timeoutSeconds} seconds (${timeoutOnNotFound} seconds for not found)..."""
        } else {
            println """Waiting for ${apiUrl} to be available in ${timeoutSeconds} seconds..."""
        }
        final int startTime = System.currentTimeMillis() / 1000
        while (!connectionOk && durationSeconds < timeoutSeconds) {
            tries++
            print "Try #${tries}..."
            HttpURLConnection connection = apiUrl.openConnection() as HttpURLConnection
            try {
                connection.connect()
                try {
                    def code = connection.getResponseCode()
                    println "Code = ${code}"
                    connectionOk = (code == HttpURLConnection.HTTP_OK)
                    // TODO Parses the JSON
                    // Error codes
                    if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        if (durationSeconds >= timeoutOnNotFound) {
                            throw new JenkinsAPINotFoundException(apiUrl)
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (SocketException ignored) {
                // Trying again...
                println "Cannot connect"
            }
            if (!connectionOk) {
                sleep INTERVAL
                durationSeconds = (System.currentTimeMillis() / 1000) - startTime
                println "Elapsed time: ${durationSeconds} seconds"
            }
        }
        // If connection is still not OK, that's a failure
        if (!connectionOk) {
            throw new JenkinsAPINotAvailableException(apiUrl)
        }
    }

}

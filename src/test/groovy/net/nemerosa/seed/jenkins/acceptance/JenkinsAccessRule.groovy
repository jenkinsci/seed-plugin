package net.nemerosa.seed.jenkins.acceptance

import groovy.json.JsonSlurper
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import java.util.regex.Pattern

import static net.nemerosa.seed.jenkins.acceptance.Until.until

class JenkinsAccessRule implements TestRule {

    /**
     * Retry interval in milliseconds
     */
    static final int INTERVAL = 5 * 1000L

    /**
     * Build number pattern in the Location field when creating a build
     */
    static final Pattern LOCATION_BUILD_NUMBER = Pattern.compile(/(\d+)\/$/)

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
        api(jobPath(path), timeoutSeconds, timeoutOnNotFound)
    }

    protected static String jobPath(String path) {
        String jobPath = path.replace('/', '/job/')
        "job/${jobPath}"
    }

    /**
     * Fires a job with a set of parameters
     */
    def fireJob(String path, Map<String, String> parameters = [:], int timeoutSeconds = 120) {
        // Build path
        String buildPath
        if (parameters && parameters.size() > 0) {
            String query = parameters.collect { entry -> "${entry.key}=${entry.value}" }.join('&')
            buildPath = "${jobPath(path)}/buildWithParameters?${query}"
        } else {
            buildPath = "${jobPath(path)}/build"
        }
        // Fires the build
        fireBuild buildPath, timeoutSeconds
    }

    /**
     * Fires a build
     */
    protected def fireBuild(String path, int timeoutSeconds = 120) {
        def url = new URL(jenkinsUrl, path)
        def connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = 'POST'
        // TODO Credentials
        println "Firing build at ${url}"
        connection.connect()
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_CREATED) {
                // The `Location` header contains the link to the queued build
                String location = connection.getHeaderField('Location')
                if (!location) throw new JenkinsAPIBuildException(path, "Location header was not returned")
                println "Queue item at: ${location}"
                // Waits until the item is built
                def buildUrl = until(timeoutSeconds).every(5) {
                    println "Checking if the build is scheduled for ${location}..."
                    def json = callUrl(new URL(location + "api/json"), 10, 10)
                    if (json.executable) {
                        return json.executable.url
                    } else {
                        return false
                    }
                }
                // Build URL
                if (buildUrl) {
                    println "Build available at ${buildUrl}"
                    until(timeoutSeconds).every(5) {
                        println "Checking if the build is finished at ${buildUrl}..."
                        def json = callUrl(new URL(buildUrl + "api/json"), 10, 10)
                        if (!json.building && json.result == 'SUCCESS') {
                            return json
                        } else {
                            return false
                        }
                    }
                } else {
                    throw new JenkinsAPIBuildException(path, "Build not scheduled after ${timeoutSeconds} seconds")
                }
            } else {
                throw new JenkinsAPIBuildException(path, "Build not fired: ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }

    public def api(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        String prefix
        if (path.endsWith('/')) {
            prefix = path
        } else {
            prefix = path + '/'
        }
        def apiUrl = new URL(jenkinsUrl, "${prefix}api/json")
        callUrl(apiUrl, timeoutSeconds, timeoutOnNotFound)
    }

    public static def callUrl(URL url, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {

        if (timeoutOnNotFound && timeoutOnNotFound != timeoutSeconds) {
            println """Waiting for ${url} to be available in ${timeoutSeconds} seconds (${
                timeoutOnNotFound
            } seconds for not found)..."""
        } else {
            println """Waiting for ${url} to be available in ${timeoutSeconds} seconds..."""
        }

        until(timeoutSeconds).every(5) { int duration ->
            HttpURLConnection connection = url.openConnection() as HttpURLConnection
            try {
                connection.connect()
                try {
                    def code = connection.getResponseCode()
                    println "Code = ${code}"
                    // Parses the JSON
                    if (code == HttpURLConnection.HTTP_OK) {
                        def content = connection.inputStream.text
                        if (content) {
                            return new JsonSlurper().parseText(content)
                        } else {
                            println "No content returned"
                            return false
                        }
                    }
                    // Error codes
                    else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        if (duration >= timeoutOnNotFound) {
                            throw new JenkinsAPINotFoundException(url)
                        } else {
                            println "Timeout on not found not reached yet"
                            return false
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (SocketException ignored) {
                // Trying again...
                println "Cannot connect"
                return false
            }
        }
    }

}

package net.nemerosa.seed.jenkins.acceptance

import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import static net.nemerosa.seed.jenkins.acceptance.Until.until

class JenkinsAccessRule implements TestRule {

    URL jenkinsUrl

    static void info(String message) {
        println "* ${message}"
    }

    static void debug(String message) {
        println "    ${message}"
    }

    @Override
    Statement apply(Statement base, Description description) {
        // Jenkins end point
        jenkinsUrl = new URL(System.getProperty('jenkinsUrl', 'http://localhost:8080'))
        info """[test] Running "${description.methodName}" against ${jenkinsUrl}"""
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

    public void job(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 60) {
        info """[job] Getting job at ${path}"""
        api(jobPath(path), timeoutSeconds, timeoutOnNotFound)
    }

    protected static String jobPath(String path) {
        String jobPath = path.replace('/', '/job/')
        "job/${jobPath}"
    }

    /**
     * Fires a job with a set of parameters
     */
    Build fireJob(String path, Map<String, String> parameters = [:], int timeoutSeconds = 120) {
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
    protected Build fireBuild(String path, int timeoutSeconds = 120) {
        info "[build] Firing build at ${path}"
        def url = new URL(jenkinsUrl, path)
        def connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = 'POST'
        connection.connect()
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_CREATED) {
                // The `Location` header contains the link to the queued build
                String location = connection.getHeaderField('Location')
                if (!location) throw new JenkinsAPIBuildException(path, "Location header was not returned")
                debug "Queue item at: ${location}"
                // Waits until the item is built
                def buildUrl = until(timeoutSeconds).every(5) {
                    debug "Checking if the build is scheduled for ${location}..."
                    def json = callUrl(new URL(location + "api/json"), 10, 10)
                    if (json.executable) {
                        return json.executable.url
                    } else {
                        return false
                    }
                }
                // Build URL
                if (buildUrl) {
                    debug "Build available at ${buildUrl}"
                    until(timeoutSeconds).every(5) {
                        debug "Checking if the build is finished at ${buildUrl}..."
                        def json = callUrl(new URL(buildUrl + "api/json"), 10, 10)
                        if (json.result) {
                            return new Build(json)
                        } else {
                            return false
                        }
                    } as Build
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
            debug """Waiting for ${url} to be available in ${timeoutSeconds} seconds (${
                timeoutOnNotFound
            } seconds for not found)"""
        } else {
            debug """Waiting for ${url} to be available in ${timeoutSeconds} seconds"""
        }

        until(timeoutSeconds).every(5) { int duration ->
            HttpURLConnection connection = url.openConnection() as HttpURLConnection
            try {
                connection.connect()
                try {
                    def code = connection.getResponseCode()
                    debug "Code = ${code}"
                    // Parses the JSON
                    if (code == HttpURLConnection.HTTP_OK) {
                        def content = connection.inputStream.text
                        if (content) {
                            return new JsonSlurper().parseText(content)
                        } else {
                            debug "No content returned"
                            return false
                        }
                    }
                    // Error codes
                    else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        if (duration >= timeoutOnNotFound) {
                            throw new JenkinsAPINotFoundException(url)
                        } else {
                            debug "Timeout on not found not reached yet"
                            return false
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (SocketException ignored) {
                // Trying again...
                debug "Cannot connect"
                return false
            }
        }
    }

    /**
     * Gets a job/folder configuration as XML
     */
    def jobConfig(String job) {
        String path = jobPath(job)
        URL url = new URL(jenkinsUrl, "${path}/config.xml")
        url.openStream().withStream {
            new XmlSlurper().parse(it)
        }
    }

    void configureSeed(String yaml) {
        def url = new URL(jenkinsUrl, "seed-config/")
        info "[config] Updating Seed configuration at ${url}..."
        def connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = 'POST'
        connection.doOutput = true
        connection.connect()
        try {
            connection.outputStream.write(yaml.getBytes('UTF-8'))
            connection.outputStream.flush()
            // Reads the response
            assert (connection.responseCode == HttpURLConnection.HTTP_OK): "Seed configuration failed with code: ${connection.responseCode}"
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Gets access to a build, waiting for it to be available first.
     */
    Build getBuild(String path, int buildNumber, int timeoutSeconds = 120) {
        def url = new URL(jenkinsUrl, jobPath(path) + "/${buildNumber}/api/json")
        def json = callUrl(url, timeoutSeconds, timeoutSeconds)
        return new Build(json)
    }

    class Build {

        final def json

        Build(json) {
            this.json = json
        }

        void checkSuccess() {
            if (json.result != 'SUCCESS') {
                // Gets the console output
                String output = new URL(json.url + 'consoleText').text
                System.err.println(output)
                // Error
                Assert.fail("${json.url} resulted in ${json.result}")
            }
        }
    }

}

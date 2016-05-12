package net.nemerosa.jenkins.seed.test

import groovy.json.JsonSlurper
import net.nemerosa.jenkins.seed.acceptance.SeedDSLGenerator
import net.nemerosa.jenkins.seed.config.PipelineConfig
import org.junit.Assert
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class JenkinsAccessRule implements TestRule {

    URL jenkinsUrl

    static void info(String message) {
        println "* ${message}"
    }

    static void debug(String message) {
        println "    ${message}"
    }

    static void trace(String message) {
        println "      - ${message}"
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

    public void gone(String path, int timeoutSeconds = 120) {
        info """[job] Testing job presence at ${path}"""
        try {
            api(jobPath(path), timeoutSeconds, 0)
            // Nope - the path should have been gone
            throw new JenkinsAPIFoundException(path)
        } catch (JenkinsAPINotFoundException ignored) {
            // OK - the path is gone
        }
    }

    protected static String jobPath(String path) {
        String jobPath = path.replace('/', '/job/')
        "job/${jobPath}"
    }

    /**
     * Fires a job with a set of parameters
     */
    Build fireJob(String path, Map<String, String> parameters = [:], int timeoutSeconds = 120) {
        info "[fire] Firing job at ${path}"
        // Build path
        String query = null
        String buildPath
        if (parameters && parameters.size() > 0) {
            query = parameters.collect { entry -> "${entry.key}=${entry.value}" }.join('&')
            buildPath = "${jobPath(path)}/buildWithParameters"
        } else {
            buildPath = "${jobPath(path)}/build"
        }
        // Fires the build
        fireBuild buildPath, timeoutSeconds, query
    }

    /**
     * Fires a build
     */
    protected Build fireBuild(String path, int timeoutSeconds = 120, String query = null) {
        def url = new URL(jenkinsUrl, path)
        def connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = 'POST'
        if (query) {
            connection.setRequestProperty('Content-Type', 'application/x-www-form-urlencoded')
            connection.doOutput = true
            connection.outputStream.withStream {
                it.write(query.bytes)
            }
        }
        connection.connect()
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_CREATED) {
                // The `Location` header contains the link to the queued build
                String location = connection.getHeaderField('Location')
                if (!location) throw new JenkinsAPIBuildException(path, "Location header was not returned")
                debug "Queue item at: ${location}"
                // Waits until the item is built
                def buildUrl = Until.until(timeoutSeconds).every(5) {
                    debug "Checking if the build is scheduled for ${location}..."
                    def json = callUrl(new URL(location + "api/json"), 10, 10)
                    if (json.executable) {
                        return json.executable.url
                    } else {
                        return false
                    }
                } as String
                // Build URL
                if (buildUrl) {
                    debug "Build available at ${buildUrl}"
                    waitForBuild(buildUrl, timeoutSeconds)
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

    protected Build waitForBuild(String buildUrl, int timeoutSeconds) {
        Until.until(timeoutSeconds).every(5) {
            debug "Checking if the build is finished at ${buildUrl}..."
            def json = callUrl(new URL(buildUrl + "/api/json"), timeoutSeconds, timeoutSeconds)
            if (json.result) {
                return new Build(json)
            } else {
                return false
            }
        } as Build
    }

    public def api(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        callUrl(url(path, "api/json"), timeoutSeconds, timeoutOnNotFound)
    }

    protected URL url(String path, String suffix = '') {
        String prefix
        if (suffix) {
            if (path.endsWith('/')) {
                prefix = path
            } else {
                prefix = path + '/'
            }
        } else {
            prefix = path
        }
        new URL(jenkinsUrl, "${prefix}" + suffix)
    }

    public def post(String path, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        post(path, {}, timeoutSeconds, timeoutOnNotFound)
    }

    public def post(String path, Closure connectionSetup, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        info "[post] Posting to ${path}"
        callUrl(url(path), { HttpURLConnection c ->
            c.requestMethod = 'POST'
            connectionSetup(c)
        }, timeoutSeconds, timeoutOnNotFound)
    }

    public static def callUrl(URL url, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {
        callUrl(url, {}, timeoutSeconds, timeoutOnNotFound)
    }

    protected static
    def callUrl(URL url, Closure connectionSetup, int timeoutSeconds = 120, int timeoutOnNotFound = 0) {

        if (timeoutOnNotFound && timeoutOnNotFound != timeoutSeconds) {
            trace """Waiting for ${url} to be available in ${timeoutSeconds} seconds (${
                timeoutOnNotFound
            } seconds for not found)"""
        } else {
            trace """Waiting for ${url} to be available in ${timeoutSeconds} seconds"""
        }

        Until.until(timeoutSeconds).every(5) { int duration ->
            HttpURLConnection connection = url.openConnection() as HttpURLConnection
            try {
                connectionSetup(connection)
                connection.connect()
                try {
                    def code = connection.getResponseCode()
                    trace "Code = ${code}"
                    // Parses the JSON
                    if (code == HttpURLConnection.HTTP_OK ||
                            code == HttpURLConnection.HTTP_CREATED ||
                            code == HttpURLConnection.HTTP_ACCEPTED) {
                        def content = connection.inputStream.text
                        if (content) {
                            trace "Page OK"
                            return new JsonSlurper().parseText(content)
                        } else {
                            trace "No content returned"
                            return false
                        }
                    }
                    // Not found
                    else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        if (duration >= timeoutOnNotFound) {
                            throw new JenkinsAPINotFoundException(url)
                        } else {
                            trace "Timeout on not found not reached yet"
                            return false
                        }
                    }
                    // Not authorised
                    else if (code == HttpURLConnection.HTTP_FORBIDDEN) {
                        throw new JenkinsAPIRefusedException()
                    }
                    // Internal error
                    else if (code == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        throw new JenkinsAPIException(
                                url,
                                "[${code}] ${connection.responseMessage}"
                        )
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (SocketException ignored) {
                // Trying again...
                trace "Cannot connect"
                return false
            }
        }
    }

    /**
     * Gets a job/folder configuration as XML
     */
    def jobConfig(String job) {
        info "[job] Getting job config for ${job}"
        String path = jobPath(job)
        URL url = new URL(jenkinsUrl, "${path}/config.xml")
        url.openStream().withStream {
            new XmlSlurper().parse(it)
        }
    }

    /**
     * Gets the content of a file in a job's workspace
     */
    String getWorkspaceFile(String job, String filePath) {
        info "[job] Getting ${filePath} file for ${job}"
        String path = jobPath(job)
        URL url = new URL(jenkinsUrl, "${path}/ws/${filePath}/*view*/")
        return url.text
    }

    @Deprecated
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
        info "[build] Getting build ${buildNumber} for ${path}"
        def url = new URL(jenkinsUrl, jobPath(path) + "/${buildNumber}")
        return waitForBuild(url as String, timeoutSeconds)
    }

    /**
     * Creates a seed job
     * @return Name of the generated seed job
     */
    String seed(PipelineConfig config, String jobName = null) {
        String name = jobName ?: TestUtils.uid('seed-')
        info "[seed] Generating seed job: ${jobName}"
        fireJob("seed-generator", [DSL: SeedDSLGenerator.seedDsl(name, config)]).checkSuccess()
        return name
    }

    String defaultSeed() {
        // Creates a seed job
        String seed = seed(PipelineConfig.defaultConfig())
        // ... checks it is there
        job(seed)
        // Returns its name
        return seed
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

        void checkFailure() {
            if (json.result != 'FAILURE') {
                Assert.fail("${json.url} resulted in ${json.result} while FAILURE was expected")
            }
        }

        String getOutput() {
            new URL((json.url as String) + 'consoleText').text
        }
    }

}

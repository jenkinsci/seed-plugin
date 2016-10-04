package net.nemerosa.jenkins.seed.generator

import hudson.EnvVars
import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.model.ParametersAction
import hudson.model.StringParameterValue
import net.nemerosa.jenkins.seed.config.PipelineGeneratorScriptNotAllowedException
import org.apache.commons.lang.StringUtils

import static SeedProperties.SEED_DSL_LIBRARIES
import static net.nemerosa.jenkins.seed.generator.SeedProperties.*

class PipelineGeneration {

    private final String project
    private final String scmType
    private final String scmUrl
    private final String scmCredentials
    private final String branch
    private final String seedProject
    private final String seedBranch
    private final boolean disableDslScript
    private final String scriptDirectory

    PipelineGeneration(String project, String scmType, String scmUrl, String scmCredentials, String branch, String seedProject, String seedBranch, boolean disableDslScript, String scriptDirectory) {
        this.branch = branch
        this.scmCredentials = scmCredentials
        this.scmUrl = scmUrl
        this.scmType = scmType
        this.project = project
        this.seedProject = seedProject
        this.seedBranch = seedBranch
        this.disableDslScript = disableDslScript
        this.scriptDirectory = scriptDirectory
    }

    boolean perform(AbstractBuild build, BuildListener listener) {

        // Path to the directory containing the pipeline definition files
        String dirPath = StringUtils.isNotBlank(scriptDirectory) ? scriptDirectory : 'seed'

        // Path to the properties
        String propertiesPath = dirPath + '/seed.properties'

        // Reads the property file (if it exists)
        Properties properties = new Properties()
        def propertyFile = build.workspace.child(propertiesPath)
        if (propertyFile.exists()) {
            listener.logger.println("[seed] Reading properties from ${propertiesPath}...")
            propertyFile.read().withStream { properties.load(it) }
        } else {
            listener.logger.println("[seed] No property file at ${propertiesPath}.")
        }

        // Gets the list of dependencies from the property file
        List<String> dependencies = []
        String dependenciesValue = properties[SEED_DSL_LIBRARIES]
        if (dependenciesValue) {
            dependencies = dependenciesValue.split(',')
        }

        // Logging
        listener.logger.println('[seed] List of DSL dependencies:')
        dependencies.each {
            listener.logger.println("[seed] * ${it}")
        }

        // Gets the dependency source for the bootstrap script
        String dslBootstrapDependency = properties[SEED_DSL_SCRIPT_JAR]
        listener.logger.println("[seed] DSL script JAR: ${dslBootstrapDependency}")

        // Gets the location of the bootstrap script
        String dslBootstrapLocation = properties[SEED_DSL_SCRIPT_LOCATION] ?: 'seed.groovy'
        listener.logger.println("[seed] DSL script location: ${dslBootstrapLocation}")

        // Source repository
        String repositoryUrl = properties[SEED_DSL_REPOSITORY]
        String repository = generateRepositoryGradle(repositoryUrl, properties, listener)

        // Is the script extraction step needed?
        boolean scriptExtraction = StringUtils.isNotBlank(dslBootstrapDependency) || !dependencies.empty

        // Checks for authorisation to run DSL scripts
        if (!scriptExtraction && disableDslScript) {
            throw new PipelineGeneratorScriptNotAllowedException()
        }

        // Environment variables to inject
        Map<String, String> environment = [
                SEED_PROJECT           : seedProject,
                SEED_BRANCH            : seedBranch,
                SEED_GRADLE            : scriptExtraction ? 'yes' : 'no',
                BRANCH                 : branch,
                PROJECT                : project,
                PROJECT_SCM_TYPE       : scmType,
                PROJECT_SCM_URL        : scmUrl,
                PROJECT_SCM_CREDENTIALS: scmCredentials,
                SEED_GROOVY_PATH       : getSeedGroovyPath(),
        ]

        // Logging
        environment.each { key, value ->
            listener.logger.println("[seed] Env: ${key} --> ${value}")
        }

        // Injects the environment variables
        build.addAction(new ParametersAction(
                environment.collect { key, value ->
                    new StringParameterValue(key, value)
                }
        ))
        def vars = build.getEnvironment(listener)
        vars.putAll(environment)

        // Logging
        listener.logger.println("[seed] Gradle script extraction needed: ${scriptExtraction}")

        // Prepares the Gradle environment (only if script extraction is needed)
        if (scriptExtraction) {
            def gradleDir = prepareGradleEnvironment(listener, build, dirPath)

            // Generates the build.gradle file
            String gradle = generateGradle(listener, repository, dependencies, dslBootstrapDependency, dslBootstrapLocation)
            gradleDir.child('build.gradle').write(gradle, 'UTF-8')
        }

        // OK
        return true
    }

    public String getSeedGroovyPath() {
        if (StringUtils.isNotBlank(scriptDirectory)) {
            if ("." == scriptDirectory) {
                return "seed.groovy"
            } else {
                return "${scriptDirectory}/seed.groovy"
            }
        } else {
            return 'seed/seed.groovy'
        }
    }

    public static String generateRepositoryGradle(String repositoryUrl, Properties properties, BuildListener listener) {
        String repository
        if (repositoryUrl) {
            if (repositoryUrl.startsWith('flat:')) {
                def repositoryDir = repositoryUrl - 'flat:'
                repository = "flatDir { dirs '${repositoryDir}' }"
                listener.logger.println("[seed] Using local repository at ${repositoryDir}")
            } else {
                String repositoryUser = properties[SEED_DSL_REPOSITORY_USER]
                String repositoryPassword = properties[SEED_DSL_REPOSITORY_PASSWORD]
                if (repositoryUser && repositoryPassword) {
                    repositoryUser = extractCredential(repositoryUser);
                    repositoryPassword = extractCredential(repositoryPassword);
                    repository = """\
maven {
    url '${repositoryUrl}'
    credentials {
        username ${repositoryUser}
        password ${repositoryPassword}
    }
}
"""
                    listener.logger.println("[seed] Using authenticated repository at ${repositoryUrl} using ${repositoryUser} user")
                } else {
                    // Repository without credentials
                    repository = "maven { url '${repositoryUrl}' }"
                    listener.logger.println("[seed] Using repository at ${repositoryUrl}")
                }
            }
        } else {
            repository = "mavenCentral()"
            listener.logger.println("[seed] Using Maven Central repository")
        }
        return repository
    }

    public static String generateGradle(
            BuildListener listener,
            String repository,
            List<String> dependencies,
            String dslBootstrapDependency,
            String dslBootstrapLocation) {
        listener.logger.println("[seed] Generating the Gradle file...")
        String gradle = """\
repositories {
    ${repository}
}
configurations {
    dslLibrary
}
${
            if (dependencies) {
                """\
dependencies {
    ${dependencies.collect { "dslLibrary '${it}'" }.join('\n    ')}
}"""
            } else {
                ''
            }
        }
task clean {
    delete 'lib'
}
task copyLibraries(type: Copy, dependsOn: clean) {
    into 'lib'
    from configurations.dslLibrary
}
task logLibraries(dependsOn: copyLibraries) {
    doLast {
        println "[seed][library] DSL libraries:"
        configurations.dslLibrary.resolvedConfiguration.resolvedArtifacts.each { a ->
            println "[seed][library] \${a.name} --> \${a.file.absolutePath}"
        }
    }
}
"""
        if (dslBootstrapDependency) {
            gradle += """\
task extractScript {
    dependsOn copyLibraries
    dependsOn logLibraries
    doFirst {
        ant.unzip(dest: '.') {
            fileset(dir: 'lib') {
                include(name: '${dslBootstrapDependency}*.jar')
            }
            patternset {
                include(name: '${dslBootstrapLocation}')
            }
        }
    }
}
task prepare(dependsOn: extractScript)
"""
        } else {
            gradle += """\
task prepare(dependsOn: copyLibraries)
"""
        }
        return gradle
    }

    public static FilePath prepareGradleEnvironment(BuildListener listener, AbstractBuild build, String dirPath) {
        listener.logger.println("[seed] Preparing the Gradle environment...")
        def gradleDir = build.workspace.child(dirPath)
        gradleDir.mkdirs()
        gradleDir.child('gradlew').copyFrom(PipelineGeneration.class.getResource('/gradle/gradlew'))
        gradleDir.child('gradlew.bat').copyFrom(PipelineGeneration.class.getResource('/gradle/gradlew.bat'))
        def wrapperDir = gradleDir.child('gradle/wrapper')
        wrapperDir.mkdirs()
        wrapperDir.child('gradle-wrapper.jar').copyFrom(PipelineGeneration.class.getResource('/gradle/gradle/wrapper/gradle-wrapper.jar'))
        wrapperDir.child('gradle-wrapper.properties').copyFrom(PipelineGeneration.class.getResource('/gradle/gradle/wrapper/gradle-wrapper.properties'))
        // OK
        return gradleDir
    }

    static String extractCredential(String expression) {
        if (expression.startsWith('${') && expression.endsWith('}')) {
            String variable = expression[2..-2]
            return "System.getenv('${variable}')";
        } else {
            return "'${expression}'";
        }
    }
}

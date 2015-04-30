package net.nemerosa.seed.generator.pipeline

import hudson.Extension
import hudson.Launcher
import hudson.model.*
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import net.nemerosa.seed.config.SeedDSLHelper
import net.nemerosa.seed.config.SeedProjectEnvironment
import org.kohsuke.stapler.DataBoundConstructor

/**
 * This step prepares the DSL environment for the {@link PropertiesPipelineGenerator}.
 *
 * It will:
 *
 * - read the property file
 * - get the configuration of the pipeline from 1) the property file 2) the project configuration
 *
 * Reads the property file and gets:
 *
 * <ul>
 * <li>the list of dependencies.
 * <li>the JAR containing the bootstrap script
 * <li>inject the <code>SEED_DSL_SCRIPT_LOCATION</code> environment variable, which sets the location of the DSL
 *     bootstrap script (defaults to <i>seed.groovy</i> if not defined)
 * <li>inject a few precomputed names in the environment:
 *      <ul>
 *          <li>SEED_PROJECT - normalised project name
 *          <li>SEED_BRANCH - normalised branch name
 *      </ul>
 * <li>prepare a Gradle environment in the <i>seed</i> directory
 * <li>prepare a Gradle build file to:
 *     <ul>
 *         <li>download the DSL libraries
 *         <li>extract the DSL bootstrap script from one one those libraries
 *         <li>put them all in the <i>seed/lib</i> directory
 *     </ul>
 * </ul>
 */
class PropertiesPipelineGeneratorBuilder extends Builder {

    public static final String SEED_DSL_SCRIPT_LOCATION = 'SEED_DSL_SCRIPT_LOCATION'
    public static final String SEED_PROJECT = 'SEED_PROJECT'
    public static final String SEED_BRANCH = 'SEED_BRANCH'

    private final String project
    private final String projectClass
    private final String projectScmType
    private final String projectScmUrl
    private final String branch
    private final String propertyPath

    @DataBoundConstructor
    PropertiesPipelineGeneratorBuilder(String propertyPath, String project, String projectClass, String projectScmType, String projectScmUrl, String branch) {
        this.propertyPath = propertyPath
        this.project = project
        this.projectClass = projectClass
        this.projectScmType = projectScmType
        this.projectScmUrl = projectScmUrl
        this.branch = branch
    }

    @Override
    boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        // Getting the project environment
        SeedProjectEnvironment projectEnvironment = new SeedDSLHelper().getProjectEnvironment(
                project,
                projectClass,
                projectScmType,
                projectScmUrl
        )

        // Reads the property file
        listener.logger.println("Reading properties from ${propertyPath}...")
        Properties properties = new Properties()
        build.workspace.child(propertyPath).read().withStream { properties.load(it) }

        // Gets the list of dependencies from the property file
        List<String> dependencies
        String dependenciesValue = properties['seed.dsl.libraries']
        if (dependenciesValue) {
            dependencies = dependenciesValue.split(',')
        }
        // ... or from the configuration
        else {
            dependencies = projectEnvironment.getConfigurationList('pipeline-generator-libraries')
        }
        listener.logger.println("List of DSL dependencies:")
        dependencies.each {
            listener.logger.println("* ${it}")
        }

        // Gets the dependency source for the bootstrap script
        String dslBootstrapDependency = properties['seed.dsl.script.jar']
        if (!dslBootstrapDependency) {
            dslBootstrapDependency = projectEnvironment.getConfigurationValue('pipeline-generator-script-jar', 'pipeline')
        }
        listener.logger.println("DSL script JAR: ${dslBootstrapDependency}")

        // Gets the location of the bootstrap script
        String dslBootstrapLocation = properties['seed.dsl.script.location']
        if (!dslBootstrapLocation) {
            dslBootstrapLocation = projectEnvironment.getConfigurationValue('pipeline-generator-script-location', 'seed.groovy')
        }
        listener.logger.println("DSL script location: ${dslBootstrapLocation}")

        // Source repository
        String repositoryUrl = properties['seed.dsl.repository']
        if (!repositoryUrl) {
            repositoryUrl = projectEnvironment.getConfigurationValue('pipeline-generator-repository', '')
        }
        String repository
        if (repositoryUrl) {
            if (repositoryUrl.startsWith('flat:')) {
                repository = "flatDir { dirs '${repositoryUrl - 'flat:'}' }"
            } else {
                repository = "maven { url '${repositoryUrl}' }"
            }
        } else {
            repository = "mavenCentral()"
        }

        // Computation of seed names
        String seedProjectName = projectEnvironment.projectConfiguration.name
        String seedBranchName = projectEnvironment.namingStrategy.getBranchName(branch)

        // Injects the environment variables
        build.addAction(new ParametersAction(
                new StringParameterValue(SEED_DSL_SCRIPT_LOCATION, dslBootstrapLocation),
                new StringParameterValue(SEED_PROJECT, seedProjectName),
                new StringParameterValue(SEED_BRANCH, seedBranchName),
        ))

        // Prepares the Gradle environment
        listener.logger.println("Preparing the Gradle environment...")
        def gradleDir = build.workspace.child('seed')
        gradleDir.mkdirs()
        gradleDir.child('gradlew').copyFrom(getClass().getResource('/gradle/gradlew'))
        gradleDir.child('gradlew.bat').copyFrom(getClass().getResource('/gradle/gradlew.bat'))
        def wrapperDir = gradleDir.child('gradle/wrapper')
        wrapperDir.mkdirs()
        wrapperDir.child('gradle-wrapper.jar').copyFrom(getClass().getResource('/gradle/gradle/wrapper/gradle-wrapper.jar'))
        wrapperDir.child('gradle-wrapper.properties').copyFrom(getClass().getResource('/gradle/gradle/wrapper/gradle-wrapper.properties'))

        // Generates the build.gradle file
        // TODO Extraction of the DSL script
        listener.logger.println("Generating the Gradle file...")
        String gradle = """\
repositories {
    ${repository}
}
configurations {
    dslLibrary
}
dependencies {
    ${dependencies.collect { "dslLibrary (name: '${it}', version: '+')" }.join('\n')}
}
task clean {
    delete 'lib'
}
task copyLibraries(type: Copy, dependsOn: clean) {
    into 'lib'
    from configurations.dslLibrary
}
task extractScript(dependsOn: copyLibraries) {
    doFirst {
        ant.unzip(dest: 'lib') {
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
        gradleDir.child('build.gradle').write(gradle, 'UTF-8')

        // OK
        return true
    }

    String getProject() {
        return project
    }

    String getProjectClass() {
        return projectClass
    }

    String getProjectScmType() {
        return projectScmType
    }

    String getProjectScmUrl() {
        return projectScmUrl
    }

    String getBranch() {
        return branch
    }

    String getPropertyPath() {
        return propertyPath
    }

    @Extension
    public static class PropertiesPipelineGeneratorBuilderDescription extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch pipeline generator preparation";
        }
    }
}

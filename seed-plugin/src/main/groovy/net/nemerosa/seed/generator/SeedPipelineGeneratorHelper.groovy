package net.nemerosa.seed.generator

import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.model.ParametersAction
import hudson.model.StringParameterValue
import net.nemerosa.jenkins.seed.config.PipelineGeneratorScriptNotAllowedException
import net.nemerosa.seed.config.SeedDSLHelper
import net.nemerosa.seed.config.SeedProjectEnvironment
import org.apache.commons.lang.StringUtils

import static net.nemerosa.jenkins.seed.generator.PipelineGeneration.*
import static net.nemerosa.jenkins.seed.generator.SeedProperties.*

/**
 * This class prepares the DSL environment for the {@link BranchPipelineGeneratorExtension}.
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
@Deprecated
class SeedPipelineGeneratorHelper {

    public static final String ENV_SEED_DSL_SCRIPT_LOCATION = 'SEED_DSL_SCRIPT_LOCATION'

    private final String project
    private final String projectClass
    private final String projectScmType
    private final String projectScmUrl
    private final String projectScmCredentials
    private final String branch
    private final String propertyPath

    SeedPipelineGeneratorHelper(String project, String projectClass, String projectScmType, String projectScmUrl, String projectScmCredentials, String branch, String propertyPath) {
        this.project = project
        this.projectClass = projectClass
        this.projectScmType = projectScmType
        this.projectScmUrl = projectScmUrl
        this.projectScmCredentials = projectScmCredentials
        this.branch = branch
        this.propertyPath = propertyPath
    }

    boolean perform(AbstractBuild build, BuildListener listener) throws InterruptedException, IOException {

        // Getting the project environment
        SeedProjectEnvironment projectEnvironment = new SeedDSLHelper().getProjectEnvironment(
                project,
                projectClass,
                projectScmType,
                projectScmUrl,
                projectScmCredentials,
                true
        )

        // Reads the property file
        listener.logger.println("Reading properties from ${propertyPath}...")
        Properties properties = new Properties()
        def propertyFile = build.workspace.child(propertyPath)
        if (propertyFile.exists()) {
            propertyFile.read().withStream { properties.load(it) }
        }

        // Gets the list of dependencies from the property file
        List<String> dependencies
        String dependenciesValue = properties[SEED_DSL_LIBRARIES]
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
        String dslBootstrapDependency = properties[SEED_DSL_SCRIPT_JAR]
        if (!dslBootstrapDependency) {
            dslBootstrapDependency = projectEnvironment.getConfigurationValue('pipeline-generator-script-jar', '')
        }
        listener.logger.println("DSL script JAR: ${dslBootstrapDependency}")

        // Gets the location of the bootstrap script
        String dslBootstrapLocation = properties[SEED_DSL_SCRIPT_LOCATION]
        if (!dslBootstrapLocation) {
            dslBootstrapLocation = projectEnvironment.getConfigurationValue('pipeline-generator-script-location', 'seed.groovy')
        }
        listener.logger.println("DSL script location: ${dslBootstrapLocation}")

        // Source repository
        String repositoryUrl = properties[SEED_DSL_REPOSITORY]
        if (!repositoryUrl) {
            repositoryUrl = projectEnvironment.getConfigurationValue('pipeline-generator-repository', '')
        }
        def repository = generateRepositoryGradle(repositoryUrl, properties, listener)

        // Computation of seed names
        String seedProjectName = projectEnvironment.projectConfiguration.name
        String seedBranchName = projectEnvironment.namingStrategy.getBranchName(branch)

        // Is the script extraction step needed?
        boolean scriptExtraction = StringUtils.isNotBlank(dslBootstrapDependency) || !dependencies.empty

        // Is a direct script execution allowed?
        if (!scriptExtraction && !projectEnvironment.getConfigurationBoolean('pipeline-generator-script-allowed', true)) {
            throw new PipelineGeneratorScriptNotAllowedException()
        }

        // Injects the environment variables
        build.addAction(new ParametersAction(
                new StringParameterValue(ENV_SEED_DSL_SCRIPT_LOCATION, dslBootstrapLocation),
                new StringParameterValue(ENV_SEED_PROJECT, seedProjectName),
                new StringParameterValue(ENV_SEED_BRANCH, seedBranchName),
                new StringParameterValue(ENV_SEED_GRADLE, scriptExtraction ? 'yes' : 'no'),
        ))

        // Prepares the Gradle environment
        def gradleDir = prepareGradleEnvironment(listener, build)

        // Generates the build.gradle file
        def gradle = generateGradle(listener, repository, dependencies, dslBootstrapDependency, dslBootstrapLocation)
        gradleDir.child('build.gradle').write(gradle, 'UTF-8')

        // OK
        return true
    }

}

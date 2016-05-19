package net.nemerosa.jenkins.seed.integration

import hudson.model.ChoiceParameterDefinition
import hudson.model.ParametersDefinitionProperty
import hudson.model.StringParameterDefinition
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig
import net.nemerosa.jenkins.seed.generator.ProjectGenerationStep
import net.nemerosa.jenkins.seed.test.TestUtils
import org.jvnet.hudson.test.JenkinsRule

class SeedRule extends JenkinsRule {

    /**
     * Creates a Seed job, with default settings
     */
    String defaultSeed() {
        return seed(PipelineConfig.defaultConfig())
    }

    /**
     * Creates a Seed job with the mentioned settings
     * @param pipelineConfig Configuration to set
     * @param jobName Job name (defaults to a generated name if not specified)
     * @return Name of the seed job
     */
    String seed(PipelineConfig pipelineConfig, String jobName = null) {
        // Name of the seed job
        String name = jobName ?: TestUtils.uid('seed-')
        // Creates a job
        def job = createFreeStyleProject(name)
        // Parameters
        job.addProperty(new ParametersDefinitionProperty(
                new StringParameterDefinition('PROJECT', ''),
                new ChoiceParameterDefinition('PROJECT_SCM_TYPE', ['git', 'svn'] as String[], ''),
                new StringParameterDefinition('PROJECT_SCM_URL', ''),
                new StringParameterDefinition('PROJECT_SCM_CREDENTIALS', ''),
                new ChoiceParameterDefinition('PROJECT_TRIGGER_TYPE', ['', 'github', 'bitbucket', 'http'] as String[], ''),
                new StringParameterDefinition('PROJECT_TRIGGER_SECRET', ''),
        ))
        // Generation step
        job.buildersList << new ProjectGenerationStep(
                new ProjectPipelineConfig(
                        pipelineConfig,
                        '${PROJECT}',
                        '${PROJECT_SCM_TYPE}',
                        '${PROJECT_SCM_URL}',
                        '${PROJECT_SCM_CREDENTIALS}',
                        '${PROJECT_TRIGGER_TYPE}',
                        '${PROJECT_TRIGGER_SECRET}',
                )
        )
        // OK
        return name
    }
}

package net.nemerosa.jenkins.seed.generator;

import com.google.common.collect.ImmutableMap;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

/**
 * Configuration of a Seed job when it's time to generate/update a project.
 */
public class ProjectGenerationStep extends AbstractSeedStep {

    /**
     * Pipeline configuration to pass to the projects being generated/updated.
     */
    private final ProjectPipelineConfig projectConfig;

    /**
     * Initialisation
     *
     * @param projectConfig Pipeline configuration
     */
    @DataBoundConstructor
    public ProjectGenerationStep(ProjectPipelineConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    /**
     * Gets access to the pipeline configuration
     */
    public ProjectPipelineConfig getProjectConfig() {
        return projectConfig;
    }

    @Override
    protected String getScriptPath() {
        return "/project-generation.groovy";
    }

    @Override
    protected Map<String, GenerationExtension> getExtensionPoints(EnvVars env, ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        return ImmutableMap.<String, GenerationExtension>of(
                "projectAuthorisations", new ProjectAuthorisationsGenerationExtension(projectConfig, parameters),
                "projectGeneration", new ProjectGenerationGenerationExtension(projectConfig, parameters)
        );
    }

    @Extension
    public static class SeedStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Project folder generation";
        }
    }
}

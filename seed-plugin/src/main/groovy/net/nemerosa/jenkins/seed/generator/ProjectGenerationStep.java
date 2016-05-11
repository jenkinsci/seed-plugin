package net.nemerosa.jenkins.seed.generator;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import net.nemerosa.seed.generator.ProjectFolderAuthorisationsExtension;
import net.nemerosa.seed.generator.ProjectSeedExtension;
import org.kohsuke.stapler.DataBoundConstructor;

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
    protected String replaceExtensionPoints(String script, EnvVars env, ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        String result;
        result = replaceExtensionPoint(script, "projectAuthorisations", new ProjectAuthorisationsExtension(projectConfig, parameters).generate());
        result = replaceExtensionPoint(result, "projectGeneration", new ProjectGenerationExtension(projectConfig, parameters).generate());
        return result;
    }

    @Extension
    public static class SeedStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Project pipeline generation";
        }
    }
}

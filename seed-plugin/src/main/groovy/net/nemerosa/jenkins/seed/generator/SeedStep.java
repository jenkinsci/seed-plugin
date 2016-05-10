package net.nemerosa.jenkins.seed.generator;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Configuration of a Seed job when it's time to generate/update a project.
 */
public class SeedStep extends AbstractSeedStep {

    /**
     * Pipeline configuration to pass to the projects being generated/updated.
     */
    private final PipelineConfig pipelineConfig;

    /**
     * Initialisation
     *
     * @param pipelineConfig Pipeline configuration
     */
    @DataBoundConstructor
    public SeedStep(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
    }

    /**
     * Gets access to the pipeline configuration
     */
    public PipelineConfig getPipelineConfig() {
        return pipelineConfig;
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

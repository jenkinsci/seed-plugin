package net.nemerosa.jenkins.seed.generator;

import hudson.model.Action;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import org.kohsuke.stapler.DataBoundConstructor;

public class PipelineConfigAction implements Action {

    private final PipelineConfig pipelineConfig;

    @DataBoundConstructor
    public PipelineConfigAction(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
    }

    public PipelineConfig getPipelineConfig() {
        return pipelineConfig;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }
}

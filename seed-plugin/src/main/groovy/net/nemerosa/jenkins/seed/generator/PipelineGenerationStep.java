package net.nemerosa.jenkins.seed.generator;

import com.google.common.base.Function;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

public class PipelineGenerationStep extends AbstractGenerationStep {

    private final String project;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String projectScmCredentials;
    private final String branch;
    private final String branchSeedName;

    @DataBoundConstructor
    public PipelineGenerationStep(String project, String projectScmType, String projectScmUrl, String projectScmCredentials, String branch, String branchSeedName) {
        this.project = project;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.projectScmCredentials = projectScmCredentials;
        this.branch = branch;
        this.branchSeedName = branchSeedName;
    }

    @Override
    protected String configure(Function<String, String> expandFn, Map<String, String> config, String script, EnvVars env) {
        // FIXME Method net.nemerosa.jenkins.seed.generator.PipelineGenerationStep.configure
        return script;
    }

    @Override
    protected String getScriptPath() {
        return "/pipeline-generation.groovy";
    }

    @Extension
    public static class PipelineGenerationStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch pipeline generation";
        }
    }

    public String getProject() {
        return project;
    }

    public String getProjectScmType() {
        return projectScmType;
    }

    public String getProjectScmUrl() {
        return projectScmUrl;
    }

    public String getProjectScmCredentials() {
        return projectScmCredentials;
    }

    public String getBranch() {
        return branch;
    }

    public String getBranchSeedName() {
        return branchSeedName;
    }
}

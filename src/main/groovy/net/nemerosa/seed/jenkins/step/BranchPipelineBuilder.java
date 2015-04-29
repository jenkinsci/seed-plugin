package net.nemerosa.seed.jenkins.step;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Build step which generates the pipeline for a branch
 */
public class BranchPipelineBuilder extends Builder {

    private final String project;
    private final String projectClass;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String branch;

    @DataBoundConstructor
    public BranchPipelineBuilder(String project, String projectClass, String projectScmType, String projectScmUrl, String branch) {
        this.project = project;
        this.projectClass = projectClass;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.branch = branch;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // FIXME Method net.nemerosa.seed.jenkins.step.BranchPipelineBuilder.perform
        return true;
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return DESCRIPTOR;
    }

    @SuppressWarnings("unused")
    public String getProject() {
        return project;
    }

    @SuppressWarnings("unused")
    public String getProjectClass() {
        return projectClass;
    }

    @SuppressWarnings("unused")
    public String getProjectScmType() {
        return projectScmType;
    }

    @SuppressWarnings("unused")
    public String getProjectScmUrl() {
        return projectScmUrl;
    }

    @SuppressWarnings("unused")
    public String getBranch() {
        return branch;
    }

    public static final BranchPipelineBuilderDescriptor DESCRIPTOR = new BranchPipelineBuilderDescriptor();

    @Extension
    public static class BranchPipelineBuilderDescriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch pipeline generator";
        }
    }
}

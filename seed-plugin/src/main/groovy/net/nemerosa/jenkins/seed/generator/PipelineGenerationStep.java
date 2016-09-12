package net.nemerosa.jenkins.seed.generator;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class PipelineGenerationStep extends Builder {

    private final String project;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String projectScmCredentials;
    private final String branch;
    private final String seedProject;
    private final String seedBranch;
    private final boolean disableDslScript;
    private final String scriptDirectory;

    @DataBoundConstructor
    public PipelineGenerationStep(String project, String projectScmType, String projectScmUrl, String projectScmCredentials, String branch, String seedProject, String seedBranch, boolean disableDslScript, String scriptDirectory) {
        this.project = project;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.projectScmCredentials = projectScmCredentials;
        this.branch = branch;
        this.seedProject = seedProject;
        this.seedBranch = seedBranch;
        this.disableDslScript = disableDslScript;
        this.scriptDirectory = scriptDirectory;
    }
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return new PipelineGeneration(
                project,
                projectScmType,
                projectScmUrl,
                projectScmCredentials,
                branch,
                seedProject,
                seedBranch,
                disableDslScript,
                scriptDirectory
        ).perform(build, listener);
    }

    @Extension
    public static class PipelineGenerationStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Seed - Branch pipeline generation";
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

    public String getSeedProject() {
        return seedProject;
    }

    public String getSeedBranch() {
        return seedBranch;
    }

    public boolean isDisableDslScript() {
        return disableDslScript;
    }

    public String getScriptDirectory() {
        return scriptDirectory;
    }
}

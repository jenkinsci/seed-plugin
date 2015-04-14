package net.nemerosa.seed.jenkins.step;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import static net.nemerosa.seed.jenkins.support.PluginSupport.expand;

/**
 * Build step which creates a project folder and a project seed inside.
 */
public class ProjectSeedBuilder extends Builder {

    private final String project;
    private final String projectScmType;
    private final String projectScmUrl;

    @DataBoundConstructor
    public ProjectSeedBuilder(String project, String projectScmType, String projectScmUrl) {
        this.project = project;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
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

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Gets actual properties
        String theProject = expand(project, build, listener);
        String theProjectScmType = expand(projectScmType, build, listener);
        String theProjectScmUrl = expand(projectScmUrl, build, listener);
        // FIXME Method net.nemerosa.seed.jenkins.step.ProjectSeedBuilder.perform
        return super.perform(build, launcher, listener);
    }

    @Extension
    public static class OntrackDSLStepDescription extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Project seed generator";
        }
    }
}

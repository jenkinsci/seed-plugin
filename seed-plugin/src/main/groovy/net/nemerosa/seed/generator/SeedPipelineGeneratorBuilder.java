package net.nemerosa.seed.generator;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * This step prepares the DSL environment for the {@link BranchPipelineGeneratorExtension}
 * and delegates its job to the {@link SeedPipelineGeneratorHelper} class.
 * <p/>
 * Due to some limitations in the detection of descriptors in Jenkins, the builder itself
 * must be written in Java, not in Groovy.
 */
@Deprecated
public class SeedPipelineGeneratorBuilder extends Builder {

    private final String project;
    private final String projectClass;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String projectScmCredentials;
    private final String branch;
    private final String propertyPath;

    @DataBoundConstructor
    public SeedPipelineGeneratorBuilder(String propertyPath, String project, String projectClass, String projectScmType, String projectScmUrl, String projectScmCredentials, String branch) {
        this.propertyPath = propertyPath;
        this.project = project;
        this.projectClass = projectClass;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.projectScmCredentials = projectScmCredentials;
        this.branch = branch;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return new SeedPipelineGeneratorHelper(
                project,
                projectClass,
                projectScmType,
                projectScmUrl,
                projectScmCredentials,
                branch,
                propertyPath
        ).perform(build, listener);
    }

    public String getProject() {
        return project;
    }

    public String getProjectClass() {
        return projectClass;
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

    public String getPropertyPath() {
        return propertyPath;
    }

    @Extension
    public static class SeedPipelineGeneratorBuilderDescription extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Seed - Branch pipeline generator preparation [deprecated]";
        }
    }
}

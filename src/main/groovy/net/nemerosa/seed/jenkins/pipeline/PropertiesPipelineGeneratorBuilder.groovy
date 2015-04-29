package net.nemerosa.seed.jenkins.pipeline

import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import org.kohsuke.stapler.DataBoundConstructor

/**
 * This step prepares the DSL environment for the {@link PropertiesPipelineGenerator}.
 *
 * Reads the property file and gets:
 *
 * - the list of dependencies.
 * - the JAR containing the bootstrap script
 *
 * Those properties are read from the property file first, then from the configuration, and have a default value if
 * not defined anywhere.
 *
 * The output of this build will be a set of environment variables:
 *
 * - a comma separated list of Gradle dependency notations
 * - a new line separated list of JAR paths to use for the DSL
 * - a JAR file name and a path inside it to get the DSL bootstrap script
 */
class PropertiesPipelineGeneratorBuilder extends Builder {

    private final String project
    private final String projectClass
    private final String projectScmType
    private final String projectScmUrl
    private final String branch
    private final String propertyPath

    @DataBoundConstructor
    PropertiesPipelineGeneratorBuilder(String propertyPath, String project, String projectClass, String projectScmType, String projectScmUrl, String branch) {
        this.propertyPath = propertyPath
        this.project = project
        this.projectClass = projectClass
        this.projectScmType = projectScmType
        this.projectScmUrl = projectScmUrl
        this.branch = branch
    }

    @Override
    boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // FIXME Method hudson.tasks.BuildStepCompatibilityLayer.perform
        return true
    }

    String getProject() {
        return project
    }

    String getProjectClass() {
        return projectClass
    }

    String getProjectScmType() {
        return projectScmType
    }

    String getProjectScmUrl() {
        return projectScmUrl
    }

    String getBranch() {
        return branch
    }

    String getPropertyPath() {
        return propertyPath
    }

    @Extension
    public static class PropertiesPipelineGeneratorBuilderDescriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch pipeline generator preparation";
        }
    }
}

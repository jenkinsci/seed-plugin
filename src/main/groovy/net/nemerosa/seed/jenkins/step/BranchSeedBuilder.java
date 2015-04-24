package net.nemerosa.seed.jenkins.step;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import javaposse.jobdsl.dsl.*;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.plugin.LookupStrategy;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.support.JoinClassLoader;
import net.nemerosa.seed.jenkins.support.SeedDSLHelper;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

/**
 * Build step which creates a branch folder and a branch seed inside.
 */
public class BranchSeedBuilder extends Builder {

    private final String project;
    private final String projectClass;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String branch;

    @DataBoundConstructor
    public BranchSeedBuilder(String project, String projectClass, String projectScmType, String projectScmUrl, String branch) {
        this.project = project;
        this.projectClass = projectClass;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.branch = branch;
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return DESCRIPTOR;
    }

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

    public String getBranch() {
        return branch;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        // Environment for the DSL execution
        EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        // Gets actual properties
        String theProject = env.expand(project);
        String theProjectClass = env.expand(projectClass);
        String theProjectScmType = env.expand(projectScmType);
        String theProjectScmUrl = env.expand(projectScmUrl);
        String theBranch = env.expand(branch);

        // Adds to the environment
        env.put("PROJECT", theProject);
        env.put("PROJECT_CLASS", theProjectClass);
        env.put("PROJECT_SCM_TYPE", theProjectScmType);
        env.put("PROJECT_SCM_URL", theProjectScmUrl);
        env.put("BRANCH", theBranch);

        // Project seed generation script
        String script = SeedDSLHelper.getResourceAsText("/branch-seed-generator.groovy");

        // Jobs are created at the Jenkins root level
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger(), env, build, LookupStrategy.JENKINS_ROOT);

        // Generation request
        ScriptRequest scriptRequest = new ScriptRequest(
                null,
                script,
                new URL[]{DslClasspath.classpathFor(this.getClass())},
                false, // not ignoring existing,
                Collections.<String, Object>singletonMap("seedDSLHelper", new SeedDSLHelper())
        );

        // Combined class loader
        ClassLoader joinClassLoader = new JoinClassLoader(
                DslScriptLoader.class.getClassLoader(),
                Jenkins.getInstance().getPluginManager().uberClassLoader,
                Thread.currentThread().getContextClassLoader()
        );

        // Generation
        GeneratedItems generatedItems = DslScriptLoader.runDslEngine(
                scriptRequest,
                jm,
                joinClassLoader
        );

        // Logging
        for (GeneratedJob job : generatedItems.getJobs()) {
            listener.getLogger().format("Generated item: %s", job);
        }
        for (GeneratedView view : generatedItems.getViews()) {
            listener.getLogger().format("Generated view: %s", view);
        }

        // Done
        Jenkins.getInstance().rebuildDependencyGraph();

        return true;
    }

    public static final BranchSeedBuilderDescriptor DESCRIPTOR = new BranchSeedBuilderDescriptor();

    @Extension
    public static class BranchSeedBuilderDescriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch seed generator";
        }
    }
}

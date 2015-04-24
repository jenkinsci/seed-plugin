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
import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper;
import net.nemerosa.seed.jenkins.support.JoinClassLoader;
import net.nemerosa.seed.jenkins.support.SeedDSLHelper;
import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

/**
 * Build step which creates a project folder and a project seed inside.
 */
public class ProjectSeedBuilder extends Builder {

    private final String project;
    private final String projectClass;
    private final String projectScmType;
    private final String projectScmUrl;

    @DataBoundConstructor
    public ProjectSeedBuilder(String project, String projectClass, String projectScmType, String projectScmUrl) {
        this.project = project;
        this.projectClass = projectClass;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
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

        // Adds to the environment
        env.put("PROJECT", theProject);
        env.put("PROJECT_CLASS", theProjectClass);
        env.put("PROJECT_SCM_TYPE", theProjectScmType);
        env.put("PROJECT_SCM_URL", theProjectScmUrl);

        // Project helper
        SeedDSLHelper helper = new SeedDSLHelper();
        SeedProjectEnvironment projectEnvironment = helper.getProjectEnvironment(
                theProject,
                theProjectClass,
                theProjectScmType,
                theProjectScmUrl
        );

        // Configuration of the DSL script
        env.put("projectSeedFolder", SeedNamingStrategyHelper.getProjectSeedFolder(
                projectEnvironment.getNamingStrategy(),
                theProject
        ));
        env.put("projectSeedPath", projectEnvironment.getNamingStrategy().getProjectSeed(
                theProject
        ));

        // Project seed generation script
        String script = SeedDSLHelper.getResourceAsText("/project-seed-generator.groovy");

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

    public static final ProjectSeedBuilderDescriptor DESCRIPTOR = new ProjectSeedBuilderDescriptor();

    @Extension
    public static class ProjectSeedBuilderDescriptor extends BuildStepDescriptor<Builder> {

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

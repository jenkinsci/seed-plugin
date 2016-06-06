package net.nemerosa.seed.generator;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.support.DSLHelper;
import net.nemerosa.seed.config.SeedDSLHelper;
import net.nemerosa.seed.config.SeedProjectEnvironment;

import java.io.IOException;

/**
 * Build step which can generates other jobs and folders.
 */
@Deprecated
public abstract class AbstractSeedBuilder extends Builder {

    private final String project;
    private final String projectClass;
    private final String projectScmType;
    private final String projectScmUrl;
    private final String projectScmCredentials;

    protected AbstractSeedBuilder(String project, String projectClass, String projectScmType, String projectScmUrl, String projectScmCredentials) {
        this.project = project;
        this.projectClass = projectClass;
        this.projectScmType = projectScmType;
        this.projectScmUrl = projectScmUrl;
        this.projectScmCredentials = projectScmCredentials;
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

    @SuppressWarnings("unused")
    public String getProjectScmCredentials() {
        return projectScmCredentials;
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
        String theProjectScmCredentials = env.expand(projectScmCredentials);

        // Adds to the environment
        env.put("PROJECT", theProject);
        env.put("PROJECT_CLASS", theProjectClass);
        env.put("PROJECT_SCM_TYPE", theProjectScmType);
        env.put("PROJECT_SCM_URL", theProjectScmUrl);
        env.put("PROJECT_SCM_CREDENTIALS", theProjectScmCredentials);

        // Project helper
        SeedDSLHelper helper = new SeedDSLHelper();
        SeedProjectEnvironment projectEnvironment = helper.getProjectEnvironment(
                theProject,
                theProjectClass,
                theProjectScmType,
                theProjectScmUrl,
                theProjectScmCredentials,
                useConfigurationCache()
        );

        // Configuration of the DSL script
        configureEnvironment(env, projectEnvironment);
        // Project seed generation script
        String script = SeedDSLHelper.getResourceAsText(getScriptPath());

        // Replacements
        script = replaceExtensionPoints(script, env, projectEnvironment);

        // Launching the generation
        DSLHelper.launchGenerationScript(build, listener, env, script);

        // Post generation
        afterGeneration(projectEnvironment);

        // Done
        return true;
    }

    protected abstract boolean useConfigurationCache();

    protected void afterGeneration(SeedProjectEnvironment projectEnvironment) {
    }

    protected String replaceExtensionPoint(String script, String extensionPoint, String extension) {
        return script.replace(
                String.format("%sExtensionPoint()", extensionPoint),
                extension
        );
    }

    protected abstract String replaceExtensionPoints(String script, EnvVars env, SeedProjectEnvironment projectEnvironment);

    protected abstract void configureEnvironment(EnvVars env, SeedProjectEnvironment projectEnvironment);

    protected abstract String getScriptPath();
}

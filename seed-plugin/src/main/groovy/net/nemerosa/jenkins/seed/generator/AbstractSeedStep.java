package net.nemerosa.jenkins.seed.generator;

import com.google.common.base.Function;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import net.nemerosa.jenkins.seed.support.DSLHelper;
import net.nemerosa.seed.config.SeedDSLHelper;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSeedStep extends Builder {

    /**
     * Generation of the project folder and project seed.
     *
     * @param build    Seed job
     * @param launcher Its launcher
     * @param listener Its listener
     * @return State of the execution
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        // Default environment for the DSL execution
        final EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        // Function to expand the values
        Function<String, String> expandFn = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return env.expand(input);
            }
        };

        // Gets the project configuration
        ProjectPipelineConfig projectConfig = getProjectConfig();

        // Project parameters
        ProjectParameters parameters = projectConfig.getProjectParameters(expandFn);

        // General configuration
        Map<String, String> config = new HashMap<>();
        configuration(projectConfig, parameters, config, env);

        // Traces
        for (Map.Entry<String, String> entry : config.entrySet()) {
            listener.getLogger().format("Config: %s: %s%n", entry.getKey(), entry.getValue());
        }
        env.putAll(config);

        // Generation script
        String scriptPath = getScriptPath();
        listener.getLogger().format("Script: %s%n", scriptPath);
        String script = IOUtils.toString(SeedDSLHelper.class.getResource(scriptPath));

        // Replacements of extension points
        script = replaceExtensionPoints(script, env, projectConfig, parameters);

        // Saves the script
        build.getWorkspace().child("dsl.groovy").write(script, "UTF-8");

        // Runs the script
        DSLHelper.launchGenerationScript(build, listener, env, script);

        // OK
        return true;
    }

    protected abstract String getScriptPath();

    protected void configuration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config, EnvVars env) {
        generalConfiguration(parameters, config);
        pipelineConfiguration(projectConfig, parameters, config);
        projectConfiguration(projectConfig, parameters, config);
        branchConfiguration(projectConfig, parameters, config, env);
        eventConfiguration(projectConfig, parameters, config);
    }

    protected void pipelineConfiguration(ProjectPipelineConfig projectConfig, @SuppressWarnings("UnusedParameters") ProjectParameters parameters, Map<String, String> config) {
        config.put("PIPELINE_DESTRUCTOR", String.valueOf(projectConfig.getPipelineConfig().isDestructor()));
        config.put("PIPELINE_COMMIT_PARARAMETER", projectConfig.getPipelineConfig().getCommitParameter());
        config.put("PIPELINE_BRANCH_SCM_PARAMETER", String.valueOf(projectConfig.getPipelineConfig().isBranchSCMParameter()));
        config.put("PIPELINE_BRANCH_PARAMETERS", projectConfig.getPipelineConfig().getBranchParameters());
        config.put("PIPELINE_GENERATION_EXTENSION", projectConfig.getPipelineConfig().getGenerationExtension());
    }

    protected void projectConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT_FOLDER_PATH", projectConfig.getPipelineConfig().getProjectFolder(parameters));
        config.put("PROJECT_SEED_NAME", projectConfig.getPipelineConfig().getProjectSeedJob(parameters));
        config.put("PROJECT_DESTRUCTOR_NAME", String.valueOf(projectConfig.getPipelineConfig().getProjectDestructorJob(parameters)));
    }

    protected void branchConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config, EnvVars env) {
        config.put("BRANCH_FOLDER_PATH", projectConfig.getPipelineConfig().getNamingStrategy().getBranchFolderPath());
        config.put("BRANCH_SEED_NAME", projectConfig.getPipelineConfig().getNamingStrategy().getBranchSeedName());
        config.put("BRANCH_START_NAME", String.valueOf(projectConfig.getPipelineConfig().getNamingStrategy().getBranchStartName()));
        config.put("BRANCH_NAME", String.valueOf(projectConfig.getPipelineConfig().getNamingStrategy().getBranchName()));
    }

    protected void generalConfiguration(ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT", parameters.getProject());
        config.put("PROJECT_SCM_TYPE", parameters.getScmType());
        config.put("PROJECT_SCM_URL", parameters.getScmUrl());
        config.put("PROJECT_SCM_CREDENTIALS", parameters.getScmCredentials());
    }

    protected void eventConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config) {
        config.put("EVENT_STRATEGY_DELETE", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isDelete()));
        config.put("EVENT_STRATEGY_AUTO", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isAuto()));
        config.put("EVENT_STRATEGY_TRIGGER", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isTrigger()));
        config.put("EVENT_STRATEGY_START_AUTO", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isStartAuto()));
        config.put("EVENT_STRATEGY_COMMIT", projectConfig.getPipelineConfig().getEventStrategy().getCommit());
    }

    protected String replaceExtensionPoint(String script, String extensionPoint, String extension) {
        return script.replace(
                String.format("%sExtensionPoint()", extensionPoint),
                extension
        );
    }

    protected abstract String replaceExtensionPoints(String script, EnvVars env, ProjectPipelineConfig projectConfig, ProjectParameters parameters);

    protected abstract ProjectPipelineConfig getProjectConfig();
}

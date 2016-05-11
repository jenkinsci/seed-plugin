package net.nemerosa.jenkins.seed.generator;

import com.google.common.base.Function;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;

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
        configuration(projectConfig, parameters, config);

        // TODO Extensions: authorisations, etc.

        // Traces
        for (Map.Entry<String, String> entry : config.entrySet()) {
            listener.getLogger().format("Config: %s: %s", entry.getKey(), entry.getValue());
        }
        env.putAll(config);

        // TODO Runs the script

        // OK
        return true;
    }

    protected void configuration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config) {
        generalConfiguration(parameters, config);
        projectConfiguration(projectConfig, parameters, config);
    }

    private void projectConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT_FOLDER", projectConfig.getPipelineConfig().getProjectFolder(parameters));
        config.put("PROJECT_SEED_JOB", projectConfig.getPipelineConfig().getProjectSeedJob(parameters));
        config.put("PROJECT_DESTRUCTOR_ENABLED", String.valueOf(projectConfig.getPipelineConfig().isDestructor()));
        config.put("PROJECT_DESTRUCTOR_JOB", String.valueOf(projectConfig.getPipelineConfig().getProjectDestructorJob(parameters)));
    }

    private void generalConfiguration(ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT", parameters.getProject());
        config.put("PROJECT_SCM_TYPE", parameters.getScmType());
        config.put("PROJECT_SCM_BASE", parameters.getScmBase());
        config.put("PROJECT_SCM_CREDENTIALS", parameters.getScmCredentials());
    }

    protected abstract ProjectPipelineConfig getProjectConfig();
}

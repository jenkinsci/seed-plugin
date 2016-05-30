package net.nemerosa.jenkins.seed.generator;

import com.google.common.base.Function;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSeedStep extends AbstractGenerationStep {

    protected abstract String getScriptPath();

    @Override
    protected GenerationContext configure(Function<String, String> expandFn, EnvVars env) {
        // Environment variables
        Map<String, String> config = new LinkedHashMap<>();
        // Project pipeline
        final ProjectPipelineConfig projectConfig = getProjectConfig();
        // Project actual parameters
        final ProjectParameters parameters = projectConfig.getProjectParameters(expandFn);
        // Environment variables
        configuration(projectConfig, parameters, config, env);
        // Script replacements
        Map<String, GenerationExtension> extensions = getExtensionPoints(env, projectConfig, parameters);
        // OK
        return new GenerationContext(
                config,
                extensions,
                new GenerationPostProcessing() {
                    @Override
                    public void run(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env) {
                        postProcessing(projectConfig, parameters, build, listener, env);
                    }
                }
        );
    }

    /**
     * Does nothing by default
     */
    protected void postProcessing(ProjectPipelineConfig projectConfig, ProjectParameters parameters, AbstractBuild<?, ?> build, BuildListener listener, EnvVars env) {
    }

    protected void configuration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config, EnvVars env) {
        generalConfiguration(parameters, config);
        pipelineConfiguration(projectConfig, parameters, config);
        projectConfiguration(projectConfig, parameters, config);
        branchConfiguration(projectConfig, parameters, config, env);
        eventConfiguration(projectConfig, parameters, config);
    }

    protected void pipelineConfiguration(ProjectPipelineConfig projectConfig, @SuppressWarnings("UnusedParameters") ProjectParameters parameters, Map<String, String> config) {
        config.put("PIPELINE_DESTRUCTOR", String.valueOf(projectConfig.getPipelineConfig().isDestructor()));
        config.put("PIPELINE_COMMIT_PARAMETER", Objects.toString(projectConfig.getPipelineConfig().getCommitParameter(), ""));
        config.put("PIPELINE_AUTHORISATIONS", Objects.toString(projectConfig.getPipelineConfig().getAuthorisations(), ""));
        config.put("PIPELINE_BRANCH_SCM_PARAMETER", String.valueOf(projectConfig.getPipelineConfig().isBranchSCMParameter()));
        config.put("PIPELINE_BRANCH_PARAMETERS", Objects.toString(projectConfig.getPipelineConfig().getBranchParameters(), ""));
        config.put("PIPELINE_GENERATION_EXTENSION", Objects.toString(projectConfig.getPipelineConfig().getGenerationExtension(), ""));
        config.put("PIPELINE_PIPELINE_GENERATION_EXTENSION", Objects.toString(projectConfig.getPipelineConfig().getPipelineGenerationExtension(), ""));
        config.put("PIPELINE_DISABLE_DSL_SCRIPT", String.valueOf(projectConfig.getPipelineConfig().isDisableDslScript()));
        config.put("IGNORED_BRANCH_PREFIXES", Objects.toString(projectConfig.getPipelineConfig().getNamingStrategy().getIgnoredBranchPrefixes(), ""));
    }

    protected void projectConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT_FOLDER_PATH", Objects.toString(projectConfig.getPipelineConfig().getProjectFolder(parameters.getProject()), ""));
        config.put("SEED_PROJECT", Objects.toString(projectConfig.getPipelineConfig().getProjectFolder(parameters.getProject()), ""));
        config.put("PROJECT_SEED_NAME", Objects.toString(projectConfig.getPipelineConfig().getProjectSeedJob(parameters.getProject()), ""));
        config.put("PROJECT_DESTRUCTOR_NAME", String.valueOf(projectConfig.getPipelineConfig().getProjectDestructorJob(parameters.getProject())));
    }

    protected void branchConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config, EnvVars env) {
        config.put("BRANCH_FOLDER_PATH", Objects.toString(projectConfig.getPipelineConfig().getNamingStrategy().getBranchFolderPath(), ""));
        config.put("BRANCH_SEED_NAME", Objects.toString(projectConfig.getPipelineConfig().getNamingStrategy().getBranchSeedName(), ""));
        config.put("BRANCH_START_NAME", String.valueOf(projectConfig.getPipelineConfig().getNamingStrategy().getBranchStartName()));
        config.put("BRANCH_NAME", String.valueOf(projectConfig.getPipelineConfig().getNamingStrategy().getBranchName()));
    }

    protected void generalConfiguration(ProjectParameters parameters, Map<String, String> config) {
        config.put("PROJECT", Objects.toString(parameters.getProject(), ""));
        config.put("PROJECT_SCM_TYPE", Objects.toString(parameters.getScmType(), ""));
        config.put("PROJECT_SCM_URL", Objects.toString(parameters.getScmUrl(), ""));
        config.put("PROJECT_SCM_CREDENTIALS", Objects.toString(parameters.getScmCredentials(), ""));
        config.put("PROJECT_TRIGGER_TYPE", Objects.toString(parameters.getTriggerType(), ""));
        config.put("PROJECT_TRIGGER_SECRET", Objects.toString(parameters.getTriggerSecret(), ""));
    }

    protected void eventConfiguration(ProjectPipelineConfig projectConfig, @SuppressWarnings("UnusedParameters") ProjectParameters parameters, Map<String, String> config) {
        config.put("EVENT_STRATEGY_DELETE", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isDelete()));
        config.put("EVENT_STRATEGY_AUTO", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isAuto()));
        config.put("EVENT_STRATEGY_TRIGGER", String.valueOf(projectConfig.getPipelineConfig().getEventStrategy().isTrigger()));
        config.put("EVENT_STRATEGY_COMMIT", Objects.toString(projectConfig.getPipelineConfig().getEventStrategy().getCommit(), ""));
    }

    protected abstract Map<String, GenerationExtension> getExtensionPoints(EnvVars env, ProjectPipelineConfig projectConfig, ProjectParameters parameters);

    protected abstract ProjectPipelineConfig getProjectConfig();
}

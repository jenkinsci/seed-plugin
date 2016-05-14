package net.nemerosa.jenkins.seed.generator;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

/**
 * Configuration of a Project job when it's time to generate/update a branch.
 */
public class BranchGenerationStep extends AbstractSeedStep {

    /**
     * Pipeline configuration for the project.
     */
    private final ProjectPipelineConfig projectConfig;

    /**
     * Initialisation
     *
     * @param projectConfig Pipeline configuration
     */
    @DataBoundConstructor
    public BranchGenerationStep(ProjectPipelineConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    /**
     * Gets access to the pipeline configuration
     */
    public ProjectPipelineConfig getProjectConfig() {
        return projectConfig;
    }

    @Override
    protected String getScriptPath() {
        return "/branch-generation.groovy";
    }

    /**
     * The {@code env} environment map contains, among standard Jenkins build parameters, the <code>BRANCH</code>
     * name, and any additional parameter defined by the project seed configuration (<code>BRANCH_SCM</code> for
     * example, or any other custom parameter). Those environment variables do not need to be injected directly
     * since they will be injected automatically.
     * <p>
     * Only the computed paths need to be computed and injected.
     */
    @Override
    protected void branchConfiguration(ProjectPipelineConfig projectConfig, ProjectParameters parameters, Map<String, String> config, EnvVars env) {
        String branch = env.get("BRANCH", null);
        if (StringUtils.isBlank(branch)) {
            throw new MissingParameterException("BRANCH");
        }
        config.put("BRANCH_FOLDER_PATH", projectConfig.getPipelineConfig().getBranchFolderPath(parameters, branch));
        config.put("BRANCH_SEED_NAME", projectConfig.getPipelineConfig().getBranchSeedName(parameters, branch));
        config.put("BRANCH_START_NAME", String.valueOf(projectConfig.getPipelineConfig().getBranchStartName(parameters, branch)));
    }

    @Override
    protected String replaceExtensionPoints(String script, EnvVars env, ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        String result = "";
        // TODO Branch extensions
//        result = replaceExtensionPoint(script, "projectAuthorisations", new ProjectAuthorisationsExtension(projectConfig, parameters).generate());
//        result = replaceExtensionPoint(result, "projectGeneration", new ProjectGenerationExtension(projectConfig, parameters).generate());
        return result;
    }

    @Extension
    public static class BranchGenerationStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch folder generation";
        }
    }
}

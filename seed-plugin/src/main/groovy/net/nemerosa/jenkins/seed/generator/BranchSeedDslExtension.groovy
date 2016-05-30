package net.nemerosa.jenkins.seed.generator

import hudson.EnvVars
import net.nemerosa.jenkins.seed.config.ProjectParameters
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig

public class BranchSeedDslExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;
    private final String branchParameter;
    private final EnvVars env;

    public BranchSeedDslExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters, String branchParameter, EnvVars env) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
        this.branchParameter = branchParameter
        this.env = env
    }

    @Override
    public String generate() {
        return projectConfig.pipelineConfig.pipelineGenerationExtension
    }

}

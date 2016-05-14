package net.nemerosa.jenkins.seed.generator;

import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;

public class BranchSeedSCMExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;
    private final String branchParameter;

    public BranchSeedSCMExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters, String branchParameter) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
        this.branchParameter = branchParameter;
    }

    @Override
    public String generate() {
        // FIXME Method net.nemerosa.jenkins.seed.generator.BranchSeedSCMExtention.generate
        return "";
    }
}

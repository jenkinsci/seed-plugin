package net.nemerosa.jenkins.seed.generator

import hudson.EnvVars
import net.nemerosa.jenkins.seed.config.ProjectParameters
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig

public class BranchSeedParametersExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;
    private final String branchParameter;
    private final EnvVars env;

    public BranchSeedParametersExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters, String branchParameter, EnvVars env) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
        this.branchParameter = branchParameter;
        this.env = env;
    }

    @Override
    public String generate() {
        // Gets the list of branch parameters
        List<String> params = []

        // Branch SCM?
        if (projectConfig.pipelineConfig.branchSCMParameter) {
            params << 'BRANCH_SCM'
        }

        // Extra branch parameters?
        projectConfig.pipelineConfig.getBranchParameters(parameters).each { name, description ->
            params << name
        }

        // Injecting their values
        // The generated `env` call contains a fixed name and the value of the property when this DSL
        // is actually run, i.e. the value which has been select/entered by the user.
        if (params) {
            return """\
environmentVariables {
    ${params.collect { "env('${it}', ${it})" }.join('\n    ')}
}
"""
        }
        // Nothing
        else {
            return ''
        }
    }
}

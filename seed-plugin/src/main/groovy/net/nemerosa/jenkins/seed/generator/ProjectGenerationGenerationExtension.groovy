package net.nemerosa.jenkins.seed.generator

import net.nemerosa.jenkins.seed.config.ProjectParameters
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig

public class ProjectGenerationGenerationExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;

    public ProjectGenerationGenerationExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
    }

    @Override
    public String generate() {

        // Snippets
        List<String> snippets = []

        // Project seed parameters
        Map<String, String> jobParameters = new LinkedHashMap<>();
        // Branch SCM extension
        if (projectConfig.pipelineConfig.branchSCMParameter) {
            jobParameters.put("BRANCH_SCM", "Path to the SCM branch");
        }
        // Arbitrary branch parameters
        jobParameters.putAll(projectConfig.pipelineConfig.getBranchParameters(parameters));

        // Generating parameter requests
        if (!jobParameters.empty) {
            snippets << """\
parameters {
    ${jobParameters.collect { name, description -> "stringParam('${name}', '', '${description}')" }.join('\n    ')}
}
"""
        }

        // Arbitrary DSL to inject
        String dsl = projectConfig.pipelineConfig.generationExtension
        if (dsl) {
            snippets << dsl
        }

        // OK
        return snippets.join('\n')
    }
}

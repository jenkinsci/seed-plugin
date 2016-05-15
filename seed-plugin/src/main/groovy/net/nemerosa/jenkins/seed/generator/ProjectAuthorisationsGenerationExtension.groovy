package net.nemerosa.jenkins.seed.generator

import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.config.ProjectParameters

class ProjectAuthorisationsGenerationExtension implements GenerationExtension {
    private final PipelineConfig pipelineConfig;
    private final ProjectParameters parameters;

    public ProjectAuthorisationsGenerationExtension(PipelineConfig pipelineConfig, ProjectParameters parameters) {
        this.pipelineConfig = pipelineConfig;
        this.parameters = parameters;
    }

    @Override
    public String generate() {
        List<String> authorisations = pipelineConfig.getProjectAuthorisations(parameters);
        if (authorisations) {
            return """\
authorization {
    ${authorisations.collect { it -> "permission('${it}')" }.join('\n    ')}
}
"""
        } else {
            return ''
        }
    }
}

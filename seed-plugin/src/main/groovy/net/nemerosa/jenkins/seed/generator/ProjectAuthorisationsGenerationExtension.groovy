package net.nemerosa.jenkins.seed.generator

import net.nemerosa.jenkins.seed.config.ProjectParameters
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig

class ProjectAuthorisationsGenerationExtension implements GenerationExtension {
    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;

    public ProjectAuthorisationsGenerationExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
    }

    @Override
    public String generate() {
        List<String> authorisations = projectConfig.getPipelineConfig().getProjectAuthorisations(parameters);
        if (authorisations != null && !authorisations.isEmpty()) {
            return """\
authorisations {
    ${authorisations.collect { authorisation -> "   permission('${authorisation}')" }.join('\n')}
}
"""
        } else {
            return ''
        }
    }
}

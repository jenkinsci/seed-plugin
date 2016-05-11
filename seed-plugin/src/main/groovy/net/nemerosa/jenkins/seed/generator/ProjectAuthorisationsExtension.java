package net.nemerosa.jenkins.seed.generator;

import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;

import java.util.List;

public class ProjectAuthorisationsExtension {
    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;

    public ProjectAuthorisationsExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
    }


    public String generate() {
        List<String> authorisations = projectConfig.getPipelineConfig().getProjectAuthorisations(parameters);
        if (authorisations != null && !authorisations.isEmpty()) {
            StringBuilder s = new StringBuilder("authorisations {\n");
            for (String authorisation : authorisations) {
                s.append(String.format("    permission('%s')", authorisation)).append("\n");
            }
            s.append("}\n");
            return s.toString();
        } else {
            return "";
        }
    }
}

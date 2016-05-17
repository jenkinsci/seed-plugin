package net.nemerosa.jenkins.seed.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Cached parameters for a project.
 */
@Data
@AllArgsConstructor
public class ProjectSeed {

    /**
     * Name of the project
     */
    private final String project;

    /**
     * Type of trigger end point being enabled
     */
    private final String triggerType;

    /**
     * Trigger secret token
     */
    private final String triggerSecret;

    public ProjectSeed(ProjectParameters parameters) {
        this(
                parameters.getProject(),
                parameters.getTriggerType(),
                parameters.getTriggerSecret()
        );
    }
}
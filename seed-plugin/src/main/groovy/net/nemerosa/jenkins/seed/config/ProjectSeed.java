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
     * Trigger identifier, used to identify the project in the event sent by
     * the trigger source. If blank, the project name is taken instead.
     */
    private final String triggerIdentifier;

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
                parameters.getTriggerIdentifier(),
                parameters.getTriggerType(),
                parameters.getTriggerSecret()
        );
    }
}
package net.nemerosa.jenkins.seed.config;

import lombok.Data;

/**
 * Actual parameters for a project.
 */
@Data
public class ProjectParameters {

    /**
     * Name of the project
     */
    private final String project;

    /**
     * SCM type
     */
    private final String scmType;

    /**
     * SCM base (without any branch)
     */
    private final String scmUrl;

    /**
     * SCM credentials (ref. to Jenkins credentials)
     */
    private final String scmCredentials;

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

}
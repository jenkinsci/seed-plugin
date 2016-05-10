package net.nemerosa.jenkins.seed.config;

import lombok.Data;

/**
 * Configuration for a project.
 */
@Data
public class ProjectPipelineConfig {

    /**
     * General pipeline configuration, set by a Seed job.
     */
    private PipelineConfig pipelineConfig = new PipelineConfig();

    /**
     * Name of the project
     */
    private String project;

    /**
     * SCM type
     */
    private String scmType;

    /**
     * SCM base (without any branch)
     */
    private String scmBase;

    /**
     * SCM credentials (ref. to Jenkins credentials)
     */
    private String scmCredentials;

}

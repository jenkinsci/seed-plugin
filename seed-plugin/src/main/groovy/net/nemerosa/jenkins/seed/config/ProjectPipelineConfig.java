package net.nemerosa.jenkins.seed.config;

import com.google.common.base.Function;
import lombok.Data;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Configuration for a project.
 */
@Data
public class ProjectPipelineConfig {

    /**
     * General pipeline configuration, set by a Seed job.
     */
    private final PipelineConfig pipelineConfig;

    /**
     * Name of the project
     */
    private final String project;

    /**
     * SCM type
     */
    private final String scmType;

    /**
     * SCM URL (without any branch)
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
     * Trigger type (non blank is enabled)
     */
    private final String triggerType;

    /**
     * Trigger secret
     */
    private final String triggerSecret;

    @DataBoundConstructor
    public ProjectPipelineConfig(PipelineConfig pipelineConfig, String project, String scmType, String scmUrl, String scmCredentials, String triggerIdentifier, String triggerType, String triggerSecret) {
        this.pipelineConfig = pipelineConfig;
        this.project = project;
        this.scmType = scmType;
        this.scmUrl = scmUrl;
        this.scmCredentials = scmCredentials;
        this.triggerIdentifier = triggerIdentifier;
        this.triggerType = triggerType;
        this.triggerSecret = triggerSecret;
    }

    /**
     * Gets the parameters for a project
     */
    public ProjectParameters getProjectParameters(Function<String, String> expandFn) {
        return new ProjectParameters(
                expandFn.apply(project),
                expandFn.apply(scmType),
                expandFn.apply(scmUrl),
                expandFn.apply(scmCredentials),
                expandFn.apply(triggerIdentifier),
                expandFn.apply(triggerType),
                expandFn.apply(triggerSecret)
        );
    }

}
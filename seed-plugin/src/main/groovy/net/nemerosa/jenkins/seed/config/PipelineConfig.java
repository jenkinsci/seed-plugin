package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import org.kohsuke.stapler.DataBoundConstructor;

@Data
public class PipelineConfig {

    /**
     * Boolean to enable the creation of a destructor job.
     */
    private final boolean destructor;

    /**
     * Parameter to pass to the branch start
     */
    private final String commitParameter;

    /**
     * Naming strategy
     */
    private final NamingStrategyConfig namingStrategy;

    /**
     * Events configurations
     */
    private final EventStrategyConfig eventStrategy;

    @DataBoundConstructor
    public PipelineConfig(boolean destructor, String commitParameter, NamingStrategyConfig namingStrategy, EventStrategyConfig eventStrategy) {
        this.destructor = destructor;
        this.commitParameter = commitParameter;
        this.namingStrategy = namingStrategy;
        this.eventStrategy = eventStrategy;
    }

    public String getProjectFolder(ProjectParameters parameters) {
        return namingStrategy.getProjectFolder(parameters);
    }

    public String getProjectSeedJob(ProjectParameters parameters) {
        return namingStrategy.getProjectSeedJob(parameters);
    }

    public String getProjectDestructorJob(ProjectParameters parameters) {
        return namingStrategy.getProjectDestructorJob(parameters);
    }
}

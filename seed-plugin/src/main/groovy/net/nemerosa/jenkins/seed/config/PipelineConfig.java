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
     * Naming strategy
     */
    private final NamingStrategyConfig namingStrategy;

    /**
     * Events configurations
     */
    private final EventStrategyConfig eventStrategy;

    @DataBoundConstructor
    public PipelineConfig(boolean destructor, NamingStrategyConfig namingStrategy, EventStrategyConfig eventStrategy) {
        this.destructor = destructor;
        this.namingStrategy = namingStrategy;
        this.eventStrategy = eventStrategy;
    }
}

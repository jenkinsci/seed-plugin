package net.nemerosa.jenkins.seed.config;

import lombok.Data;

@Data
public class PipelineConfig {

    /**
     * Boolean to enable the creation of a destructor job.
     */
    private boolean destructor = false;

    /**
     * Naming strategy
     */
    private NamingStrategyConfig namingStrategy = new NamingStrategyConfig();

    /**
     * Events configurations
     */
    private EventStrategyConfig eventStrategy = new EventStrategyConfig();

}

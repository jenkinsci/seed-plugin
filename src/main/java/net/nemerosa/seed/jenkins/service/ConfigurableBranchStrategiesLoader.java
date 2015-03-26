package net.nemerosa.seed.jenkins.service;

import hudson.Extension;
import net.nemerosa.seed.jenkins.strategy.BranchStrategiesLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Gets a list of configurable branch strategies.
 *
 * @see net.nemerosa.seed.jenkins.strategy.configurable.ConfigurableBranchStrategy
 */
@Extension
public class ConfigurableBranchStrategiesLoader implements BranchStrategiesLoader {

    private final List<ConfigurableBranchStrategyConfiguration> branchStrategyConfigurations =
            new ArrayList<ConfigurableBranchStrategyConfiguration>();

    /**
     * List of configurations
     */
    public List<ConfigurableBranchStrategyConfiguration> getBranchStrategyConfigurations() {
        return branchStrategyConfigurations;
    }

    @Override
    public Collection<BranchStrategy> load() {
        // FIXME Method net.nemerosa.seed.jenkins.service.ConfigurableBranchStrategiesLoader.load
        return Collections.emptyList();
    }

}

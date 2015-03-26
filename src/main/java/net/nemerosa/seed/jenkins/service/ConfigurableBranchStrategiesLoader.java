package net.nemerosa.seed.jenkins.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import hudson.Extension;
import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.strategy.BranchStrategiesLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.configurable.ConfigurableBranchStrategy;

import java.util.Collection;

/**
 * Gets a list of configurable branch strategies.
 *
 * @see net.nemerosa.seed.jenkins.strategy.configurable.ConfigurableBranchStrategy
 */
@Extension
public class ConfigurableBranchStrategiesLoader implements BranchStrategiesLoader {

    @Override
    public Collection<BranchStrategy> load(SeedConfiguration configuration) {
        return Collections2.transform(
                configuration.getConfigurableBranchStrategyConfigurations().values(),
                new Function<ConfigurableBranchStrategyConfiguration, BranchStrategy>() {
                    @Override
                    public BranchStrategy apply(ConfigurableBranchStrategyConfiguration input) {
                        return new ConfigurableBranchStrategy(input);
                    }
                }
        );
    }

}

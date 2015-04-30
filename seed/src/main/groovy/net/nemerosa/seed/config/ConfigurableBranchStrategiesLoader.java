package net.nemerosa.seed.config;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import hudson.Extension;

import java.util.Collection;

/**
 * Gets a list of configurable branch strategies.
 *
 * @see ConfigurableBranchStrategy
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

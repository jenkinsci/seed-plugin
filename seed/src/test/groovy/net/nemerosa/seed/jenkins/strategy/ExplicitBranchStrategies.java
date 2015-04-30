package net.nemerosa.seed.jenkins.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.nemerosa.seed.config.SeedConfiguration;
import org.apache.commons.lang.StringUtils;

public class ExplicitBranchStrategies implements BranchStrategies {

    private final BranchStrategiesLoader loader;

    public ExplicitBranchStrategies(BranchStrategiesLoader loader) {
        this.loader = loader;
    }

    @Override
    public BranchStrategy get(final String branchStrategyId, SeedConfiguration configuration) {
        return Iterables.find(
                loader.load(configuration),
                new Predicate<BranchStrategy>() {
                    @Override
                    public boolean apply(BranchStrategy input) {
                        return StringUtils.equals(branchStrategyId, input.getId());
                    }
                }
        );
    }
}

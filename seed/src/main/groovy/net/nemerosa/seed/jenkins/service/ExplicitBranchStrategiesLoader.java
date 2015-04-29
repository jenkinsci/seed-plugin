package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.strategy.BranchStrategiesLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;

import java.util.Collection;
import java.util.List;

public class ExplicitBranchStrategiesLoader implements BranchStrategiesLoader {

    private final List<BranchStrategy> branchStrategies;

    public ExplicitBranchStrategiesLoader(List<BranchStrategy> branchStrategies) {
        this.branchStrategies = branchStrategies;
    }

    @Override
    public Collection<BranchStrategy> load(SeedConfiguration configuration) {
        return branchStrategies;
    }
}

package net.nemerosa.seed.jenkins.strategy;

import net.nemerosa.seed.jenkins.model.SeedConfiguration;

public interface BranchStrategies {

    BranchStrategy get(String branchStrategyId, SeedConfiguration configuration);

}

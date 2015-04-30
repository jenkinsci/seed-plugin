package net.nemerosa.seed.jenkins.strategy;

import net.nemerosa.seed.config.SeedConfiguration;

public interface BranchStrategies {

    BranchStrategy get(String branchStrategyId, SeedConfiguration configuration);

}

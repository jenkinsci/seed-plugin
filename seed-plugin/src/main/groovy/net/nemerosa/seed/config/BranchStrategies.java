package net.nemerosa.seed.config;

public interface BranchStrategies {

    BranchStrategy get(String branchStrategyId, SeedConfiguration configuration);

}

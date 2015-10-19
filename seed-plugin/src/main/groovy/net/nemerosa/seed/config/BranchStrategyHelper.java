package net.nemerosa.seed.config;

public final class BranchStrategyHelper {

    private BranchStrategyHelper() {
    }

    public static BranchStrategy getBranchStrategy(
            SeedConfiguration configuration,
            SeedProjectConfiguration projectConfiguration,
            BranchStrategies branchStrategies) {
        return branchStrategies.get(
                Configuration.getValue(
                        ProjectProperties.BRANCH_STRATEGY,
                        projectConfiguration,
                        configuration,
                        "seed"
                ),
                configuration
        );
    }
}

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
                        "branch-strategy",
                        projectConfiguration,
                        configuration,
                        "seed"
                ),
                configuration
        );
    }
}

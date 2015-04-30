package net.nemerosa.seed.jenkins.strategy;

import net.nemerosa.seed.config.Configuration;
import net.nemerosa.seed.config.SeedConfiguration;
import net.nemerosa.seed.config.SeedProjectConfiguration;

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

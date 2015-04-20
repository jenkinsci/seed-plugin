package net.nemerosa.seed.jenkins.strategy;

import net.nemerosa.seed.jenkins.model.Configuration;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;

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

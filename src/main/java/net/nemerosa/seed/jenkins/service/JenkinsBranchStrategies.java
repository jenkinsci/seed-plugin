package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;

/**
 * TODO Gets the list of branch strategies based on an extension point.
 */
public class JenkinsBranchStrategies implements BranchStrategies {

    @Override
    public BranchStrategy get(String branchStrategyId) {
        // TODO Uses extensions
        if ("seed".equals(branchStrategyId)) {
            return new SeedBranchStrategy();
        } else {
            throw new UnsupportedBranchStrategyException(branchStrategyId);
        }
    }
}

package net.nemerosa.seed.jenkins.strategy.naming;

import net.nemerosa.seed.config.Configuration;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;

public abstract class AbstractSeedNamingStrategy implements SeedNamingStrategy {

    @Override
    public String getBranchSeed(String project, String branch) {
        return SeedNamingStrategyHelper.getBranchPath(
                getBranchSeed(project),
                getBranchName(branch)
        );
    }

    /**
     * By default, replaces all special characters by "-"
     */
    @Override
    public String getBranchName(String branch) {
        return Configuration.normalise(branch);
    }

}

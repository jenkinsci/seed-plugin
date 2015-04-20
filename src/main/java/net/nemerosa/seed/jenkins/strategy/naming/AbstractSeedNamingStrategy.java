package net.nemerosa.seed.jenkins.strategy.naming;

import net.nemerosa.seed.jenkins.model.Configuration;
import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;

public abstract class AbstractSeedNamingStrategy implements SeedNamingStrategy {

    /**
     * By default, replaces all special characters by "-"
     */
    @Override
    public String getBranchName(String branch) {
        return Configuration.normalise(branch);
    }
    
}

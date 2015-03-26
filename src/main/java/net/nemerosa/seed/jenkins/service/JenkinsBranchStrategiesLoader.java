package net.nemerosa.seed.jenkins.service;

import hudson.Extension;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.strategy.BranchStrategiesLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;

import java.util.Collection;

/**
 * Gets the list of branch strategies based on the {@link BranchStrategy} extension point.
 */
@Extension
public class JenkinsBranchStrategiesLoader implements BranchStrategiesLoader {

    @Override
    public Collection<BranchStrategy> load(SeedConfiguration configuration) {
        return Jenkins.getInstance().getExtensionList(BranchStrategy.class);
    }
}

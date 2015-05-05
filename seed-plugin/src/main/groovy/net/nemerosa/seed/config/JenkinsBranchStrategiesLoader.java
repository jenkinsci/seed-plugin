package net.nemerosa.seed.config;

import hudson.Extension;
import jenkins.model.Jenkins;

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

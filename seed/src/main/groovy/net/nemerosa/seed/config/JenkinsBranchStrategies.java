package net.nemerosa.seed.config;

import hudson.ExtensionList;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.BranchStrategiesLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * Gets the list of branch strategies based on an extension point.
 */
public class JenkinsBranchStrategies implements BranchStrategies {

    @Override
    public BranchStrategy get(final String branchStrategyId, SeedConfiguration configuration) {
        // Gets the list of loaders
        ExtensionList<BranchStrategiesLoader> branchStrategiesLoaders =
                Jenkins.getInstance().getExtensionList(BranchStrategiesLoader.class);
        // Looking for the extension with the correct ID
        for (BranchStrategiesLoader branchStrategiesLoader : branchStrategiesLoaders) {
            Collection<BranchStrategy> branchStrategies = branchStrategiesLoader.load(configuration);
            for (BranchStrategy branchStrategy : branchStrategies) {
                if (StringUtils.equals(branchStrategyId, branchStrategy.getId())) {
                    return branchStrategy;
                }
            }
        }
        // Not found
        throw new UnsupportedBranchStrategyException(branchStrategyId);
    }
}

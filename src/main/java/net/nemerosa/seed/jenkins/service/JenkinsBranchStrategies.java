package net.nemerosa.seed.jenkins.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import hudson.ExtensionList;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import org.apache.commons.lang.StringUtils;

/**
 * Gets the list of branch strategies based on an extension point.
 */
public class JenkinsBranchStrategies implements BranchStrategies {

    @Override
    public BranchStrategy get(final String branchStrategyId) {
        // Gets the list of extensions
        ExtensionList<BranchStrategy> branchStrategies = Jenkins.getInstance().getExtensionList(BranchStrategy.class);
        // Finds the one with the same ID
        BranchStrategy selectedStrategy = Iterables.find(
                branchStrategies,
                new Predicate<BranchStrategy>() {
                    @Override
                    public boolean apply(BranchStrategy strategy) {
                        return StringUtils.equals(branchStrategyId, strategy.getId());
                    }
                },
                null
        );
        // OK
        if (selectedStrategy != null) {
            return selectedStrategy;
        } else {
            throw new UnsupportedBranchStrategyException(branchStrategyId);
        }
    }
}

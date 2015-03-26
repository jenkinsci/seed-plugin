package net.nemerosa.seed.jenkins.strategy;

import hudson.ExtensionPoint;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;

import java.util.Collection;

public interface BranchStrategiesLoader extends ExtensionPoint {

    Collection<BranchStrategy> load(SeedConfiguration configuration);

}

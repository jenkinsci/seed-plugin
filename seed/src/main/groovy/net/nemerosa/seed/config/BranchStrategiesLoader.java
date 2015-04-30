package net.nemerosa.seed.config;

import hudson.ExtensionPoint;

import java.util.Collection;

public interface BranchStrategiesLoader extends ExtensionPoint {

    Collection<BranchStrategy> load(SeedConfiguration configuration);

}

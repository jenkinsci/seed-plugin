package net.nemerosa.seed.jenkins.strategy;

import hudson.ExtensionPoint;

import java.util.Collection;

public interface BranchStrategiesLoader extends ExtensionPoint {

    Collection<BranchStrategy> load();

}

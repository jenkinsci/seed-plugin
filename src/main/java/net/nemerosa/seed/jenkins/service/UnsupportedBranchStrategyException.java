package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedException;

public class UnsupportedBranchStrategyException extends SeedException {
    public UnsupportedBranchStrategyException(String branchStrategyId) {
        super("Unsupported branch strategy: %s", branchStrategyId);
    }
}

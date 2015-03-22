package net.nemerosa.seed.jenkins.service;

public class UnsupportedBranchStrategyException extends RuntimeException {
    public UnsupportedBranchStrategyException(String branchStrategyId) {
        super(String.format("Unsupported branch strategy: %s", branchStrategyId));
    }
}

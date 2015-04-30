package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedException;

public class UnsupportedBranchStrategyException extends SeedException {
    public UnsupportedBranchStrategyException(String branchStrategyId) {
        super("Unsupported branch strategy: %s", branchStrategyId);
    }
}

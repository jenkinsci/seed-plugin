package net.nemerosa.seed.jenkins.strategy.configurable;

import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;
import net.nemerosa.seed.jenkins.strategy.naming.ConfigurableSeedNamingStrategy;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;

public class ConfigurableBranchStrategy extends SeedBranchStrategy {

    private final ConfigurableBranchStrategyConfiguration configuration;

    public ConfigurableBranchStrategy(ConfigurableBranchStrategyConfiguration configuration) {
        super(new ConfigurableSeedNamingStrategy(configuration));
        this.configuration = configuration;
    }

    @Override
    public String getId() {
        return configuration.getId();
    }

    @Override
    protected String defaultCommitParameter() {
        return configuration.getCommitParameter();
    }

}

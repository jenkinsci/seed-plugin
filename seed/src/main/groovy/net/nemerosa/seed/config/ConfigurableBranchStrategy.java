package net.nemerosa.seed.config;

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

package net.nemerosa.seed.jenkins.strategy.configurable;

import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;
import org.apache.commons.lang.StringUtils;

import static net.nemerosa.seed.jenkins.support.Evaluator.evaluate;

public class ConfigurableBranchStrategy extends SeedBranchStrategy {

    private final ConfigurableBranchStrategyConfiguration configuration;

    public ConfigurableBranchStrategy(ConfigurableBranchStrategyConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    public String getId() {
        return configuration.getId();
    }

    @Override
    protected String defaultSeed(String id) {
        return evaluate(configuration.getSeedExpression(), "project", id);
    }

    @Override
    protected String defaultBranchSeed(String id) {
        return evaluate(configuration.getBranchSeedExpression(), "project", id);
    }

    @Override
    protected String defaultBranchStart(String id) {
        return evaluate(configuration.getBranchStartExpression(), "project", id);
    }

    @Override
    protected String getBranchName(String branch) {
        String value = branch;
        for (String prefix : configuration.getBranchNamePrefixes()) {
            value = StringUtils.removeStart(value, prefix);
        }
        return evaluate(configuration.getBranchNameExpression(), "branch", value);
    }

    @Override
    protected String defaultCommitParameter() {
        return configuration.getCommitParameter();
    }

}

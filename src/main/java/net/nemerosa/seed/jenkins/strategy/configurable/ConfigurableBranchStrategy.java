package net.nemerosa.seed.jenkins.strategy.configurable;

import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;
import net.nemerosa.seed.jenkins.strategy.naming.ConfigurableSeedNamingStrategy;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;
import org.apache.commons.lang.StringUtils;

import static net.nemerosa.seed.jenkins.support.Evaluator.evaluate;

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

package net.nemerosa.seed.config;

import org.apache.commons.lang.StringUtils;

import static net.nemerosa.seed.config.Evaluator.evaluate;

public class ConfigurableSeedNamingStrategy extends AbstractSeedNamingStrategy {

    private final ConfigurableBranchStrategyConfiguration configuration;

    public ConfigurableSeedNamingStrategy(ConfigurableBranchStrategyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getProjectSeed(String id) {
        return evaluate(configuration.getSeedExpression(), "project", id);
    }

    @Override
    public String getProjectDestructor(String id) {
        return evaluate(configuration.getDestructorExpression(), "project", id);
    }

    @Override
    public String getBranchSeed(String id) {
        return evaluate(configuration.getBranchSeedExpression(), "project", id);
    }

    @Override
    public String getBranchStart(String id) {
        return evaluate(configuration.getBranchStartExpression(), "project", id);
    }

    @Override
    public String getBranchName(String branch) {
        String value = branch;
        for (String prefix : configuration.getBranchNamePrefixes()) {
            value = StringUtils.removeStart(value, prefix);
        }
        // Normalisation
        value = Configuration.normalise(value);
        // Evaluation
        return evaluate(configuration.getBranchNameExpression(), "branch", value);
    }
}

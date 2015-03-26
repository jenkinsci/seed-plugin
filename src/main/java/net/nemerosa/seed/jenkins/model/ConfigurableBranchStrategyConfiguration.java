package net.nemerosa.seed.jenkins.model;

import java.util.Map;

public class ConfigurableBranchStrategyConfiguration extends Configuration {

    private final String id;
    private final String seedExpression;
    private final String branchSeedExpression;
    private final String branchStartExpression;
    private final String branchNameExpression;
    private final String commitParameter;

    public ConfigurableBranchStrategyConfiguration(Map<String, ?> data) {
        super(data);
        this.id = getString("id");
        this.seedExpression = getString("seed-expression", false, "${project}/${project}-seed");
        this.branchSeedExpression = getString("branch-seed-expression", false, "${project}/${project}-*/${project}-*-seed");
        this.branchStartExpression = getString("branch-start-expression", false, "${project}/${project}-*/${project}-*-build");
        this.branchNameExpression = getString("branch-name-expression", false, "${branch}");
        this.commitParameter = getString("commit-parameter", false, "COMMIT");
    }

    public String getId() {
        return id;
    }

    public String getSeedExpression() {
        return seedExpression;
    }

    public String getBranchSeedExpression() {
        return branchSeedExpression;
    }

    public String getBranchStartExpression() {
        return branchStartExpression;
    }

    public String getBranchNameExpression() {
        return branchNameExpression;
    }

    public String getCommitParameter() {
        return commitParameter;
    }

    public static ConfigurableBranchStrategyConfiguration of(Map<String, ?> input) {
        return new ConfigurableBranchStrategyConfiguration(input);
    }
}

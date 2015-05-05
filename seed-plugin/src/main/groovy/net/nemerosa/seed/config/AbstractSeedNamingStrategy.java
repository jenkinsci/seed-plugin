package net.nemerosa.seed.config;

public abstract class AbstractSeedNamingStrategy implements SeedNamingStrategy {

    @Override
    public String getBranchSeed(String project, String branch) {
        return SeedNamingStrategyHelper.getBranchPath(
                getBranchSeed(project),
                getBranchName(branch)
        );
    }

    /**
     * By default, replaces all special characters by "-"
     */
    @Override
    public String getBranchName(String branch) {
        return Configuration.normalise(branch);
    }

}

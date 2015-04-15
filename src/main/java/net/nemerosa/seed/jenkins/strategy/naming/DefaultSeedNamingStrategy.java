package net.nemerosa.seed.jenkins.strategy.naming;

import static java.lang.String.format;
import static net.nemerosa.seed.jenkins.model.SeedProjectConfiguration.defaultName;

public class DefaultSeedNamingStrategy extends AbstractSeedNamingStrategy {

    @Override
    public String getProjectSeed(String id) {
        return format("%1$s/%1$s-seed", defaultName(id));
    }

    @Override
    public String getBranchSeed(String id) {
        return format("%1$s/%1$s-*/%1$s-*-seed", defaultName(id));
    }

    @Override
    public String getBranchStart(String id) {
        return format("%1$s/%1$s-*/%1$s-*-build", defaultName(id));
    }
}

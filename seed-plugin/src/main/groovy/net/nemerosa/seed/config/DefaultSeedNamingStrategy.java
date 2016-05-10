package net.nemerosa.seed.config;

import static java.lang.String.format;
import static net.nemerosa.seed.config.SeedProjectConfiguration.defaultName;

public class DefaultSeedNamingStrategy extends AbstractSeedNamingStrategy {

    @Override
    public String getProjectSeed(String id) {
        return format("%1$s/%1$s-seed", defaultName(id));
    }

    @Override
    public String getProjectDestructor(String id) {
        return format("%1$s/%1$s-destructor", defaultName(id));
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

package net.nemerosa.seed.jenkins.strategy.sample;

import hudson.Extension;
import net.nemerosa.seed.config.SeedConfiguration;
import net.nemerosa.seed.config.SeedProjectConfiguration;
import net.nemerosa.seed.config.SeedBranchStrategy;
import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;
import static net.nemerosa.seed.config.SeedProjectConfiguration.defaultName;

@Extension
public class CustomBranchStrategy extends SeedBranchStrategy {

    @Override
    public String getId() {
        return "custom";
    }

    @Override
    protected String defaultSeed(String id) {
        return format("%1$s/%1$s_GENERATOR", defaultName(id).toUpperCase());
    }

    @Override
    protected String defaultBranchSeed(String id) {
        return format("%1$s/%1$s_*/%1$s_*_GENERATOR", defaultName(id).toUpperCase());
    }

    @Override
    protected String defaultBranchStart(String id) {
        return format("%1$s/%1$s_*/%1$s_*_010_BUILD", defaultName(id));
    }

    @Override
    protected String getBranchName(String branch) {
        String name;
        if (StringUtils.startsWith(branch, "branches/")) {
            name = StringUtils.substringAfter(branch, "/");
        } else {
            name = branch;
        }
        return super.getBranchName(name);
    }

    @Override
    protected String getCommitParameter(SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        return "REVISION";
    }
}

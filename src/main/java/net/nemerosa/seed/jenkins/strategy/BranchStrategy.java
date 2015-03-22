package net.nemerosa.seed.jenkins.strategy;

import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;

public interface BranchStrategy {

    void post(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration);

}

package net.nemerosa.seed.jenkins.strategy;

import hudson.ExtensionPoint;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;

public interface BranchStrategy extends ExtensionPoint {

    /**
     * Gets the unique ID for this strategy. This ID will be referred to by the Seed configuration file.
     */
    String getId();

    /**
     * Gets the associated naming strategy.
     */
    SeedNamingStrategy getSeedNamingStrategy();

    /**
     * Resolving an event for this branching strategy.
     *
     * @param event                Event to post
     * @param seedLauncher         Connection with the seed jobs
     * @param configuration        Global configuration
     * @param projectConfiguration Specific project configuration
     */
    void post(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration);

}

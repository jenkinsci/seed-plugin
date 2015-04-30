package net.nemerosa.seed.triggering;

import net.nemerosa.seed.config.Configuration;
import net.nemerosa.seed.config.SeedConfiguration;
import net.nemerosa.seed.config.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.BranchStrategy;
import net.nemerosa.seed.jenkins.strategy.BranchStrategyHelper;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeedServiceImpl implements SeedService {

    private static final Logger LOGGER = Logger.getLogger(SeedService.class.getName());

    private final SeedConfigurationLoader configurationLoader;
    private final SeedLauncher seedLauncher;
    private final BranchStrategies branchStrategies;

    @Inject
    public SeedServiceImpl(SeedConfigurationLoader configurationLoader, SeedLauncher seedLauncher, BranchStrategies branchStrategies) {
        this.configurationLoader = configurationLoader;
        this.seedLauncher = seedLauncher;
        this.branchStrategies = branchStrategies;
    }

    @Override
    public void post(SeedEvent event) {
        // Logging
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(String.format(
                    "Event: project=%s, branch=%s, type=%s",
                    event.getProject(),
                    event.getBranch(),
                    event.getType()
            ));
        }
        // Loads the configuration
        SeedConfiguration configuration = configurationLoader.load();
        // Loads the project's configuration
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(event.getProject());
        // Gets the branch strategy
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(
                configuration,
                projectConfiguration,
                branchStrategies
        );
        // Dispatching
        branchStrategy.post(event, seedLauncher, configuration, projectConfiguration);
    }

    @Override
    public String getSecretKey(String project, String context) {
        // Property name
        String property = String.format(
                "%s-secret-key",
                context
        );
        // Environment variable name
        String env = property.replace("-", "_").toUpperCase();
        // Loads the configuration
        SeedConfiguration configuration = configurationLoader.load();
        // Loads the project's configuration
        SeedProjectConfiguration projectConfiguration = configuration.getProjectConfiguration(project);
        // Gets the secret key
        return Configuration.getValue(
                property,
                configuration,
                projectConfiguration,
                System.getenv(env)
        );
    }

}
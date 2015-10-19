package net.nemerosa.seed.triggering;

import net.nemerosa.seed.config.*;
import net.nemerosa.seed.triggering.connector.RequestNonAuthorizedException;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeedServiceImpl implements SeedService {

    private static final Logger LOGGER = Logger.getLogger(SeedService.class.getName());

    private final SeedConfigurationLoader configurationLoader;
    private final SeedProjectConfigurationCache projectConfigurationCache;
    private final SeedLauncher seedLauncher;
    private final BranchStrategies branchStrategies;

    @Inject
    public SeedServiceImpl(SeedConfigurationLoader configurationLoader,
                           SeedProjectConfigurationCache projectConfigurationCache,
                           SeedLauncher seedLauncher,
                           BranchStrategies branchStrategies) {
        this.configurationLoader = configurationLoader;
        this.projectConfigurationCache = projectConfigurationCache;
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

        // Gets the project configuration from the cache
        SeedConfiguration configuration;
        SeedProjectConfiguration projectConfiguration;
        SeedProjectSavedConfiguration cache = projectConfigurationCache.load(event.getProject());
        if (cache != null) {
            configuration = new SeedConfiguration(cache.getGlobalConfiguration());
            projectConfiguration = new SeedProjectConfiguration(cache.getProjectConfiguration());
        } else {
            configuration = configurationLoader.load();
            projectConfiguration = configuration.getProjectConfiguration(event.getProject());
        }

        // Checks the channel
        checkChannel(event, projectConfiguration, configuration);
        // Gets the branch strategy
        BranchStrategy branchStrategy = BranchStrategyHelper.getBranchStrategy(
                configuration,
                projectConfiguration,
                branchStrategies
        );
        // Logging
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(String.format(
                    "BranchStrategy: project=%s, strategy=%s",
                    event.getProject(),
                    branchStrategy.getId()
            ));
        }
        // Dispatching
        branchStrategy.post(event, seedLauncher, configuration, projectConfiguration);
    }

    protected void checkChannel(SeedEvent event, SeedProjectConfiguration projectConfiguration, SeedConfiguration configuration) {
        // Channel ID
        String channelId = event.getChannel().getId();
        // Enabled key
        String channelEnabledKey = String.format("%s-enabled", channelId);
        // Enabled?
        boolean enabled = Configuration.getBoolean(channelEnabledKey, projectConfiguration, configuration, true);
        // Check
        if (!enabled) {
            throw new RequestNonAuthorizedException();
        }
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

package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;

import javax.inject.Inject;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeedServiceImpl implements SeedService {

    private static final Logger LOGGER = Logger.getLogger(SeedService.class.getName());

    private final SeedConfigurationLoader configurationLoader;
    private final SeedLauncher seedLauncher;

    @Inject
    public SeedServiceImpl(SeedConfigurationLoader configurationLoader, SeedLauncher seedLauncher) {
        this.configurationLoader = configurationLoader;
        this.seedLauncher = seedLauncher;
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
        // Dispatching
        switch (event.getType()) {
            case CREATION:
                create(event.getProject(), event.getBranch(), configuration);
                break;
            case DELETION:
                delete(event.getProject(), event.getBranch(), configuration);
                break;
            default:
                throw new UnsupportedSeedEventType(event.getType());
        }
    }

    protected void create(String project, String branch, SeedConfiguration configuration) {
        // Gets the path to the branch seed job
        String path = configuration.getProjectSeed(project);
        // Launches the job
        seedLauncher.launch(path, Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                branch
        ));
    }

    protected void delete(String project, String branch, SeedConfiguration configuration) {
        // Gets the path to the branch seed job
        String path = configuration.getProjectSeed(project);
        // TODO Deletes the whole branch folder
        // TODO ... or deletes the seed job only
    }

}

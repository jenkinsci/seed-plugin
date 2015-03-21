package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.SeedConfiguration;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedEvent;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeedServiceImpl implements SeedService {

    private static final Logger LOGGER = Logger.getLogger(SeedService.class.getName());

    private final SeedConfiguration configuration;

    @Inject
    public SeedServiceImpl(SeedConfiguration configuration) {
        this.configuration = configuration;
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
        // FIXME Method net.nemerosa.seed.jenkins.service.SeedServiceImpl.create

    }

}

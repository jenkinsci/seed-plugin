package net.nemerosa.jenkins.seed.triggering;

import com.google.common.collect.ImmutableMap;
import net.nemerosa.jenkins.seed.cache.ProjectCachedConfig;
import net.nemerosa.jenkins.seed.cache.ProjectSeedCache;
import net.nemerosa.jenkins.seed.triggering.connector.RequestNonAuthorizedException;
import net.nemerosa.seed.config.Constants;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class SeedServiceImpl implements SeedService {

    private static final Logger LOGGER = Logger.getLogger(SeedService.class.getName());

    private final SeedLauncher seedLauncher;
    private final ProjectSeedCache seedCache;

    @Inject
    public SeedServiceImpl(SeedLauncher seedLauncher, ProjectSeedCache seedCache) {
        this.seedLauncher = seedLauncher;
        this.seedCache = seedCache;
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

        // The project configuration is stored in the project seed
        // but this job is not accessible directly using the project name
        // since naming conventions can change from project to project
        ProjectCachedConfig config = getProjectCachedConfig(event.getProject());

        // Checks the channel
        checkChannel(event, config);

        // Dispatching
        post(event, seedLauncher, config);
    }

    private void post(SeedEvent event, SeedLauncher seedLauncher, ProjectCachedConfig config) {
        switch (event.getType()) {
            case CREATION:
                create(event, seedLauncher, config);
                break;
            case DELETION:
                delete(event, seedLauncher, config);
                break;
            case SEED:
                seed(event, seedLauncher, config);
                break;
            case COMMIT:
                commit(event, seedLauncher, config);
                break;
            default:
                throw new UnsupportedSeedEventTypeException(event.getType());
        }
    }

    private void commit(SeedEvent event, SeedLauncher seedLauncher, ProjectCachedConfig config) {
        if (config.isTrigger()) {
            // Gets the path to the branch start job
            String path = config.getBranchStartJob(event.getBranch());
            // Uses the commit (must be specified in the event)
            String commit = event.getConfiguration().getString("commit", false, "HEAD");
            LOGGER.info(
                    format(
                            "Commit %s for branch %s of project %s - starting the pipeline at %s",
                            commit,
                            event.getBranch(),
                            event.getProject(),
                            path
                    )
            );
            // Launching the job
            seedLauncher.launch(event.getChannel(), path, ImmutableMap.of(
                    config.getPipelineConfig().getEventStrategy().getCommit(),
                    commit
            ));
        } else {
            LOGGER.finer(format("Commit events are not enabled for project %s", event.getProject()));
        }

    }

    private void seed(SeedEvent event, SeedLauncher seedLauncher, ProjectCachedConfig config) {
        if (config.isAuto()) {
            // Gets the path to the branch seed job
            String path = config.getBranchSeedJob(event.getBranch());
            // Logging
            LOGGER.info(
                    format(
                            "Seed files changed for branch %s of project %s - regenerating the pipeline at %s",
                            event.getBranch(),
                            event.getProject(),
                            path
                    )
            );
            // Launches the job (no parameter)
            seedLauncher.launch(event.getChannel(), path, null);
        } else {
            LOGGER.finer(format("Seed events are not enabled for project %s", event.getProject()));
        }
    }

    private void delete(SeedEvent event, SeedLauncher seedLauncher, ProjectCachedConfig config) {
        // Gets the path to the branch seed job
        String path = config.getBranchSeedJob(event.getBranch());
        // Deletes the whole branch folder
        if (config.isDelete()) {
            LOGGER.finer(format("Deletion of the branch means deletion of the pipeline for project %s", event.getProject()));
            // Gets the folder
            path = StringUtils.substringBeforeLast(path, "/");
            if (StringUtils.isNotBlank(path)) {
                seedLauncher.delete(path);
            }
        }
        // ... or deletes the seed job only
        else {
            LOGGER.finer(format("Deletion of the branch means deletion of the pipeline seed for project %s", event.getProject()));
            seedLauncher.delete(path);
        }

    }

    private void create(SeedEvent event, SeedLauncher seedLauncher, ProjectCachedConfig config) {
        LOGGER.finer(format("New branch %s for project %s - creating a new pipeline", event.getBranch(), event.getProject()));
        // Gets the path to the project seed
        String path = config.getProjectSeedJob();
        // Launches the job
        seedLauncher.launch(event.getChannel(), path, Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                event.getBranch()
        ));

    }

    private ProjectCachedConfig getProjectCachedConfig(String project) {
        // Using a cache, fed by the project seed itself
        ProjectCachedConfig config = seedCache.getProjectPipelineConfig(project);

        // If not found, use a default one
        if (config == null) {
            LOGGER.warning(String.format("Did not find any cache for project %s, using defaults.", project));
            config = new ProjectCachedConfig(project);
        }
        return config;
    }

    protected void checkChannel(SeedEvent event, ProjectCachedConfig config) {
        // Enabled?
        boolean enabled = config.isChannelEnabled(event.getChannel());
        // Check
        if (!enabled) {
            throw new RequestNonAuthorizedException();
        }
    }

    @Override
    public String getSecretKey(String project, String context) {
        // Gets the project's configuration
        ProjectCachedConfig config = getProjectCachedConfig(project);
        // Gets the secret key for this context
        return config.getSecretKey(context);
    }

}

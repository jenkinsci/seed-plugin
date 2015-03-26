package net.nemerosa.seed.jenkins.strategy.seed;

import com.google.common.collect.ImmutableMap;
import hudson.Extension;
import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.*;
import net.nemerosa.seed.jenkins.strategy.AbstractBranchStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.logging.Logger;

import static java.lang.String.format;
import static net.nemerosa.seed.jenkins.model.Configuration.normalise;
import static net.nemerosa.seed.jenkins.model.SeedProjectConfiguration.defaultName;

@Extension
public class SeedBranchStrategy extends AbstractBranchStrategy {

    private static final Logger LOGGER = Logger.getLogger(SeedBranchStrategy.class.getName());

    public static final String SEED = "seed";
    public static final String PIPELINE_SEED = "pipeline-seed";
    public static final String PIPELINE_START = "pipeline-start";
    public static final String PIPELINE_DELETE = "pipeline-delete";
    public static final String PIPELINE_AUTO = "pipeline-auto";
    public static final String PIPELINE_TRIGGER = "pipeline-trigger";
    public static final String PIPELINE_COMMIT = "pipeline-commit";

    @Override
    public String getId() {
        return "seed";
    }

    @Override
    public void post(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        switch (event.getType()) {
            case CREATION:
                create(event, seedLauncher, configuration, projectConfiguration);
                break;
            case DELETION:
                delete(event, seedLauncher, configuration, projectConfiguration);
                break;
            case SEED:
                seed(event, seedLauncher, configuration, projectConfiguration);
                break;
            case COMMIT:
                commit(event, seedLauncher, configuration, projectConfiguration);
                break;
            default:
                throw new UnsupportedSeedEventTypeException(event.getType());
        }
    }

    protected void seed(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        if (Configuration.getBoolean(PIPELINE_AUTO, projectConfiguration, configuration, true)) {
            LOGGER.finer(format("Seed files changed for branch %s of project %s - regenerating the pipeline", event.getBranch(), event.getProject()));
            // Gets the path to the branch seed job
            String path = getBranchSeedPath(projectConfiguration, event.getBranch());
            // Launches the job (no parameter)
            seedLauncher.launch(event.getChannel(), path, null);
        } else {
            LOGGER.finer(format("Seed events are not enabled for project %s", event.getProject()));
        }
    }

    protected void commit(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        if (Configuration.getBoolean(PIPELINE_TRIGGER, projectConfiguration, configuration, true)) {
            // Gets the path to the branch start job
            String path = getBranchStartPath(projectConfiguration, event.getBranch());
            // Uses the commit (must be specified in the event)
            String commit = event.getConfiguration().getString("commit", false, "HEAD");
            LOGGER.finer(format("Commit %s for branch %s of project %s - starting the pipeline", commit, event.getBranch(), event.getProject()));
            // Launching the job
            seedLauncher.launch(event.getChannel(), path, ImmutableMap.of(
                    getCommitParameter(configuration, projectConfiguration),
                    commit
            ));
        } else {
            LOGGER.finer(format("Commit events are not enabled for project %s", event.getProject()));
        }
    }

    protected String getCommitParameter(SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        return Configuration.getValue(
                PIPELINE_COMMIT,
                projectConfiguration,
                configuration,
                defaultCommitParameter()
        );
    }

    protected String defaultCommitParameter() {
        return "COMMIT";
    }

    protected void create(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        LOGGER.finer(format("New branch %s for project %s - creating a new pipeline", event.getBranch(), event.getProject()));
        // Gets the path to the branch seed job
        String path = projectConfiguration.getString(
                SEED,
                false,
                defaultSeed(projectConfiguration.getId())
        );
        // Launches the job
        seedLauncher.launch(event.getChannel(), path, Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                event.getBranch()
        ));
    }

    protected void delete(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        // Gets the path to the branch seed job
        String path = getBranchSeedPath(projectConfiguration, event.getBranch());
        // Deletes the whole branch folder
        if (Configuration.getBoolean(PIPELINE_DELETE, projectConfiguration, configuration, true)) {
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

    protected String getBranchSeedPath(SeedProjectConfiguration projectConfiguration, String branch) {
        return projectConfiguration.getString(
                PIPELINE_SEED,
                false,
                defaultBranchSeed(projectConfiguration.getId())
        ).replace("*", getBranchName(branch));
    }

    protected String getBranchStartPath(SeedProjectConfiguration projectConfiguration, String branch) {
        return projectConfiguration.getString(
                PIPELINE_START,
                false,
                defaultBranchStart(projectConfiguration.getId())
        ).replace("*", getBranchName(branch));
    }

    protected String getBranchName(String branch) {
        return normalise(branch);
    }

    protected String defaultSeed(String id) {
        return format("%1$s/%1$s-seed", defaultName(id));
    }

    protected String defaultBranchSeed(String id) {
        return format("%1$s/%1$s-*/%1$s-*-seed", defaultName(id));
    }

    protected String defaultBranchStart(String id) {
        return format("%1$s/%1$s-*/%1$s-*-build", defaultName(id));
    }
}

package net.nemerosa.seed.jenkins.strategy.seed;

import com.google.common.collect.ImmutableMap;
import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.*;
import net.nemerosa.seed.jenkins.strategy.AbstractBranchStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;

import static net.nemerosa.seed.jenkins.model.Configuration.normalise;
import static net.nemerosa.seed.jenkins.model.SeedProjectConfiguration.defaultName;

public class SeedBranchStrategy extends AbstractBranchStrategy {

    public static final String SEED = "seed";
    public static final String PIPELINE_SEED = "pipeline-seed";
    public static final String PIPELINE_START = "pipeline-start";
    public static final String PIPELINE_DELETE = "pipeline-delete";
    public static final String PIPELINE_AUTO = "pipeline-auto";
    public static final String PIPELINE_TRIGGER = "pipeline-trigger";
    public static final String PIPELINE_COMMIT = "pipeline-commit";

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
                throw new UnsupportedSeedEventType(event.getType());
        }
    }

    protected void seed(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        if (Configuration.getBoolean(PIPELINE_AUTO, projectConfiguration, configuration, true)) {
            // Gets the path to the branch seed job
            String path = getBranchSeedPath(projectConfiguration, event.getBranch());
            // Launches the job (no parameter)
            seedLauncher.launch(path, null);
        }
    }

    protected void commit(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        if (Configuration.getBoolean(PIPELINE_TRIGGER, projectConfiguration, configuration, true)) {
            // Gets the path to the branch start job
            String path = getBranchStartPath(projectConfiguration, event.getBranch());
            // Uses the commit (must be specified in the event)
            String commit = event.getConfiguration().getString("commit", false, "HEAD");
            // Launching the job
            seedLauncher.launch(path, ImmutableMap.of(
                    Configuration.getValue(
                            PIPELINE_COMMIT,
                            projectConfiguration,
                            configuration,
                            "COMMIT"
                    ),
                    commit
            ));
        }
    }

    protected void create(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        // Gets the path to the branch seed job
        String path = projectConfiguration.getString(
                SEED,
                false,
                defaultSeed(projectConfiguration.getId())
        );
        // Launches the job
        seedLauncher.launch(path, Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                event.getBranch()
        ));
    }

    protected void delete(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        // Gets the path to the branch seed job
        String path = getBranchSeedPath(projectConfiguration, event.getBranch());
        // Deletes the whole branch folder
        if (Configuration.getBoolean(PIPELINE_DELETE, projectConfiguration, configuration, true)) {
            // Gets the folder
            path = StringUtils.substringBeforeLast(path, "/");
            if (StringUtils.isNotBlank(path)) {
                seedLauncher.delete(path);
            }
        }
        // ... or deletes the seed job only
        else {
            seedLauncher.delete(path);
        }
    }

    protected String getBranchSeedPath(SeedProjectConfiguration projectConfiguration, String branch) {
        return projectConfiguration.getString(
                PIPELINE_SEED,
                false,
                defaultBranchSeed(projectConfiguration.getId())
        ).replace("*", normalise(branch));
    }

    protected String getBranchStartPath(SeedProjectConfiguration projectConfiguration, String branch) {
        return projectConfiguration.getString(
                PIPELINE_START,
                false,
                defaultBranchStart(projectConfiguration.getId())
        ).replace("*", normalise(branch));
    }

    private static String defaultSeed(String id) {
        return String.format("%1$s/%1$s-seed", defaultName(id));
    }

    private static String defaultBranchSeed(String id) {
        return String.format("%1$s/%1$s-*/%1$s-*-seed", defaultName(id));
    }

    private static String defaultBranchStart(String id) {
        return String.format("%1$s/%1$s-*/%1$s-*-build", defaultName(id));
    }
}

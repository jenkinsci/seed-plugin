package net.nemerosa.seed.jenkins.strategy.seed;

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
    public static final String BRANCH_SEED = "branch-seed";
    public static final String PIPELINE_DELETE = "pipeline-delete";

    @Override
    public void post(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        switch (event.getType()) {
            case CREATION:
                create(event, seedLauncher, configuration, projectConfiguration);
                break;
            case DELETION:
                delete(event, seedLauncher, configuration, projectConfiguration);
                break;
            default:
                throw new UnsupportedSeedEventType(event.getType());
        }
        // FIXME Method net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy.post

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
        String path = projectConfiguration.getString(
                BRANCH_SEED,
                false,
                defaultBranchSeed(projectConfiguration.getId())
        ).replace("*", normalise(event.getBranch()));
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

    private static String defaultSeed(String id) {
        return String.format("%1$s/%1$s-seed", defaultName(id));
    }

    private static String defaultBranchSeed(String id) {
        return String.format("%1$s/%1$s-*/%1$s-*-seed", defaultName(id));
    }

//    private static String defaultBranchStart(String id) {
//        return String.format("%1$s/%1$s-*/%1$s-*-build", defaultName(id));
//    }
}

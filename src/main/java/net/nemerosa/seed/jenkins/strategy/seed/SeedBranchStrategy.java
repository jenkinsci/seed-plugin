package net.nemerosa.seed.jenkins.strategy.seed;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.model.UnsupportedSeedEventType;
import net.nemerosa.seed.jenkins.strategy.AbstractBranchStrategy;

import java.util.Collections;

import static net.nemerosa.seed.jenkins.model.Configuration.normalise;
import static net.nemerosa.seed.jenkins.model.SeedProjectConfiguration.defaultName;

public class SeedBranchStrategy extends AbstractBranchStrategy {

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
                "seed",
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
                "branch-seed",
                false,
                defaultBranchSeed(projectConfiguration.getId())
        ).replace("*", normalise(event.getBranch()));
        // TODO Deletes the whole branch folder
        // TODO ... or deletes the seed job only
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

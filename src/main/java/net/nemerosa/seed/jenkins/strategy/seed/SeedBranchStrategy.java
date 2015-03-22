package net.nemerosa.seed.jenkins.strategy.seed;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import net.nemerosa.seed.jenkins.model.UnsupportedSeedEventType;
import net.nemerosa.seed.jenkins.strategy.AbstractBranchStrategy;

import java.util.Collections;

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
        String path = configuration.getProjectSeed(event.getProject());
        // Launches the job
        seedLauncher.launch(path, Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                event.getBranch()
        ));
    }

    protected void delete(SeedEvent event, SeedLauncher seedLauncher, SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
        // TODO Gets the path to the branch seed job
        // String path = configuration.getBranchSeed(project, branch);
        // TODO Deletes the whole branch folder
        // TODO ... or deletes the seed job only
    }
}

package net.nemerosa.seed.jenkins.strategy.seed;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SeedBranchStrategyTest {

    @Test
    public void post_create() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "master",
                        SeedEventType.CREATION
                ),
                launcher,
                new SeedConfiguration(Collections.<String, Object>emptyMap()),
                SeedProjectConfiguration.of("nemerosa/ontrack")
        );

        verify(launcher, times(1)).launch("ontrack/ontrack-seed", Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                "master"
        ));
    }

}

package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SeedServiceImplTest {

    @Test
    public void seed_branch_creation() {
        SeedConfiguration configuration = new SeedConfiguration(Collections.<String, Object>emptyMap());

        SeedConfigurationLoader loader = mock(SeedConfigurationLoader.class);
        when(loader.load()).thenReturn(configuration);

        BranchStrategies branchStrategies = mock(BranchStrategies.class);
        when(branchStrategies.get("seed")).thenReturn(new SeedBranchStrategy());

        SeedLauncher launcher = mock(SeedLauncher.class);

        SeedServiceImpl service = new SeedServiceImpl(loader, launcher, branchStrategies);
        service.post(new SeedEvent("nemerosa/ontrack", "master", SeedEventType.CREATION, SeedChannel.of("Test")));

        verify(launcher, times(1)).launch(SeedChannel.of("Test"), "ontrack/ontrack-seed", Collections.singletonMap("BRANCH", "master"));
    }

}

package net.nemerosa.seed.config;

import net.nemerosa.seed.triggering.*;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SeedServiceImplTest {

    public static final SeedChannel TEST_CHANNEL = SeedChannel.of("test", "Test");

    @Test
    public void seed_branch_creation() {
        SeedConfiguration configuration = new SeedConfiguration(Collections.<String, Object>emptyMap());

        SeedConfigurationLoader loader = mock(SeedConfigurationLoader.class);
        when(loader.load()).thenReturn(configuration);

        BranchStrategies branchStrategies = mock(BranchStrategies.class);
        when(branchStrategies.get(eq("seed"), any(SeedConfiguration.class))).thenReturn(new SeedBranchStrategy());

        SeedLauncher launcher = mock(SeedLauncher.class);

        SeedProjectConfigurationCache projectConfigurationCache = mock(SeedProjectConfigurationCache.class);

        SeedServiceImpl service = new SeedServiceImpl(loader, projectConfigurationCache, launcher, branchStrategies);
        service.post(new SeedEvent("nemerosa/ontrack", "master", SeedEventType.CREATION, TEST_CHANNEL));

        verify(launcher, times(1)).launch(TEST_CHANNEL, "ontrack/ontrack-seed", Collections.singletonMap("BRANCH", "master"));
    }

}

package net.nemerosa.seed.config;

import net.nemerosa.seed.triggering.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class CustomBranchStrategyTest {

    public static final SeedChannel TEST_CHANNEL = SeedChannel.of("test", "Test");
    private SeedLauncher launcher;
    private SeedService seedService;

    @Before
    public void setUp() throws IOException {
        SeedConfigurationLoader configurationLoader = mock(SeedConfigurationLoader.class);
        when(configurationLoader.load()).thenReturn(
                SeedConfiguration.parseYaml(
                        IOUtils.toString(
                                getClass().getResourceAsStream("/custom-strategy.yml"),
                                "UTF-8"
                        )
                )
        );

        SeedProjectConfigurationCache projectConfigurationCache = mock(SeedProjectConfigurationCache.class);

        launcher = mock(SeedLauncher.class);

        BranchStrategies branchStrategies = mock(BranchStrategies.class);
        when(branchStrategies.get(eq("custom"), any(SeedConfiguration.class))).thenReturn(new CustomBranchStrategy());

        seedService = new SeedServiceImpl(
                configurationLoader,
                projectConfigurationCache,
                launcher,
                branchStrategies
        );
    }

    @Test
    public void create() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.CREATION,
                        TEST_CHANNEL)
        );
        verify(launcher, times(1)).launch(TEST_CHANNEL, "PRJ/PRJ_GENERATOR", Collections.singletonMap(
                Constants.BRANCH_PARAMETER,
                "branches/R1.8.0"
        ));
    }

    @Test
    public void delete() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/FEATURE_TEST",
                        SeedEventType.DELETION,
                        TEST_CHANNEL)
        );
        verify(launcher, times(1)).delete("PRJ/PRJ_FEATURE_TEST");
    }

    @Test
    public void seed() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.SEED,
                        TEST_CHANNEL)
        );
        verify(launcher, times(1)).launch(TEST_CHANNEL, "PRJ/PRJ_R1.8.0/PRJ_R1.8.0_GENERATOR", null);
    }

    @Test
    public void commit() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.COMMIT,
                        TEST_CHANNEL).withParam("commit", "123456")
        );
        verify(launcher, times(1)).launch(TEST_CHANNEL, "PRJ/PRJ_R1.8.0/PRJ_R1.8.0_010_BUILD", Collections.singletonMap(
                "REVISION",
                "123456"
        ));
    }

}

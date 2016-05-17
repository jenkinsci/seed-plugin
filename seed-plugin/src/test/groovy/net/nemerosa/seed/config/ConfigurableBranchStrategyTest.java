package net.nemerosa.seed.config;

import net.nemerosa.jenkins.seed.triggering.SeedLauncher;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.jenkins.seed.triggering.SeedChannel;
import net.nemerosa.jenkins.seed.triggering.SeedEvent;
import net.nemerosa.jenkins.seed.triggering.SeedEventType;
import net.nemerosa.seed.triggering.SeedServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class ConfigurableBranchStrategyTest {

    public static final SeedChannel TEST_CHANNEL = SeedChannel.of("test", "Test");
    private SeedLauncher launcher;
    private SeedService seedService;

    @Before
    public void setUp() throws IOException {
        // Loads the configuration
        SeedConfiguration configuration = SeedConfiguration.parseYaml(
                IOUtils.toString(
                        getClass().getResourceAsStream("/configurable-strategy.yml"),
                        "UTF-8"
                )
        );
        // Configuration loader
        SeedConfigurationLoader configurationLoader = mock(SeedConfigurationLoader.class);
        when(configurationLoader.load()).thenReturn(configuration);

        SeedProjectConfigurationCache projectConfigurationCache = mock(SeedProjectConfigurationCache.class);

        launcher = mock(SeedLauncher.class);

        ConfigurableBranchStrategiesLoader configurableBranchStrategiesLoader = new ConfigurableBranchStrategiesLoader();
        BranchStrategies branchStrategies = new ExplicitBranchStrategies(configurableBranchStrategiesLoader);

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

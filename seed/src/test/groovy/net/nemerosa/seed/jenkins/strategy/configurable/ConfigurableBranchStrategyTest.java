package net.nemerosa.seed.jenkins.strategy.configurable;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.triggering.SeedLauncher;
import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.SeedChannel;
import net.nemerosa.seed.config.SeedConfiguration;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedEventType;
import net.nemerosa.seed.config.ConfigurableBranchStrategiesLoader;
import net.nemerosa.seed.triggering.SeedServiceImpl;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import net.nemerosa.seed.jenkins.strategy.ExplicitBranchStrategies;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class ConfigurableBranchStrategyTest {

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

        launcher = mock(SeedLauncher.class);

        ConfigurableBranchStrategiesLoader configurableBranchStrategiesLoader = new ConfigurableBranchStrategiesLoader();
        BranchStrategies branchStrategies = new ExplicitBranchStrategies(configurableBranchStrategiesLoader);

        seedService = new SeedServiceImpl(
                configurationLoader,
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
                        SeedChannel.of("Test"))
        );
        verify(launcher, times(1)).launch(SeedChannel.of("Test"), "PRJ/PRJ_GENERATOR", Collections.singletonMap(
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
                        SeedChannel.of("Test"))
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
                        SeedChannel.of("Test"))
        );
        verify(launcher, times(1)).launch(SeedChannel.of("Test"), "PRJ/PRJ_R1.8.0/PRJ_R1.8.0_GENERATOR", null);
    }

    @Test
    public void commit() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.COMMIT,
                        SeedChannel.of("Test")).withParam("commit", "123456")
        );
        verify(launcher, times(1)).launch(SeedChannel.of("Test"), "PRJ/PRJ_R1.8.0/PRJ_R1.8.0_010_BUILD", Collections.singletonMap(
                "REVISION",
                "123456"
        ));
    }

}

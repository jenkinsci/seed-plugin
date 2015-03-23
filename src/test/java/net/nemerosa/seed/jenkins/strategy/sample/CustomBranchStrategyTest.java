package net.nemerosa.seed.jenkins.strategy.sample;

import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.service.SeedServiceImpl;
import net.nemerosa.seed.jenkins.strategy.BranchStrategies;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class CustomBranchStrategyTest {

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

        launcher = mock(SeedLauncher.class);

        BranchStrategies branchStrategies = mock(BranchStrategies.class);
        when(branchStrategies.get("custom")).thenReturn(new CustomBranchStrategy());

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
                        SeedEventType.CREATION
                )
        );
        verify(launcher, times(1)).launch("PRJ/PRJ_GENERATOR", Collections.singletonMap(
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
                        SeedEventType.DELETION
                )
        );
        verify(launcher, times(1)).delete("PRJ/PRJ_FEATURE_TEST");
    }

    @Test
    public void seed() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.SEED
                )
        );
        verify(launcher, times(1)).launch("PRJ/PRJ_R1.8.0/PRJ_R1.8.0_GENERATOR", null);
    }

    @Test
    public void commit() {
        seedService.post(
                new SeedEvent(
                        "PRJ",
                        "branches/R1.8.0",
                        SeedEventType.COMMIT
                ).withParam("commit", "123456")
        );
        verify(launcher, times(1)).launch("PRJ/PRJ_R1.8.0/PRJ_R1.8.0_010_BUILD", Collections.singletonMap(
                "REVISION",
                "123456"
        ));
    }

}

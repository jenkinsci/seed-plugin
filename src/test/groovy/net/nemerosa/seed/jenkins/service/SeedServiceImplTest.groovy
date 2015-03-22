package net.nemerosa.seed.jenkins.service

import net.nemerosa.seed.jenkins.SeedConfigurationLoader
import net.nemerosa.seed.jenkins.SeedLauncher
import net.nemerosa.seed.jenkins.model.SeedConfiguration
import net.nemerosa.seed.jenkins.model.SeedEvent
import net.nemerosa.seed.jenkins.model.SeedEventType
import net.nemerosa.seed.jenkins.strategy.BranchStrategies
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy
import org.junit.Test

import static org.mockito.Mockito.*

class SeedServiceImplTest {

    @Test
    void 'Seed branch creation'() {
        SeedConfiguration configuration = new SeedConfiguration([:])

        SeedConfigurationLoader loader = mock(SeedConfigurationLoader)
        when(loader.load()).thenReturn(configuration)

        BranchStrategies branchStrategies = mock(BranchStrategies)
        when(branchStrategies.get("seed")).thenReturn(new SeedBranchStrategy())

        SeedLauncher launcher = mock(SeedLauncher)

        SeedServiceImpl service = new SeedServiceImpl(loader, launcher, branchStrategies)
        service.post(new SeedEvent("nemerosa/ontrack", "master", SeedEventType.CREATION))

        verify(launcher, times(1)).launch("ontrack/ontrack-seed", Collections.singletonMap("BRANCH", "master"))
    }

}

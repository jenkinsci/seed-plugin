package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.SeedConfigurationLoader;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SeedServiceImplTest {

    @Test
    public void create() {
        SeedConfigurationLoader loader = mock(SeedConfigurationLoader.class);
        SeedConfiguration configuration = new SeedConfiguration(Collections.<SeedProjectConfiguration>emptyList());
        SeedLauncher launcher = mock(SeedLauncher.class);
        SeedServiceImpl service = new SeedServiceImpl(loader, launcher);
        service.create("nemerosa/ontrack", "master", configuration);
        verify(launcher, times(1)).launch("ontrack/ontrack-seed", Collections.singletonMap("BRANCH", "master"));
    }

}

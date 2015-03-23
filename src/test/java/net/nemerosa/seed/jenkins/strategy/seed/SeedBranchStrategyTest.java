package net.nemerosa.seed.jenkins.strategy.seed;

import com.google.common.collect.ImmutableMap;
import net.nemerosa.seed.jenkins.Constants;
import net.nemerosa.seed.jenkins.SeedLauncher;
import net.nemerosa.seed.jenkins.model.SeedConfiguration;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Collections;
import java.util.Map;

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

    @Test
    public void post_delete_default() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.DELETION
                ),
                launcher,
                new SeedConfiguration(Collections.<String, Object>emptyMap()),
                SeedProjectConfiguration.of("nemerosa/ontrack")
        );

        verify(launcher, times(1)).delete("ontrack/ontrack-feature-xxx");
    }

    @Test
    public void post_delete_pipeline_not_deleted_global_config() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.DELETION
                ),
                launcher,
                new SeedConfiguration(Collections.singletonMap("pipeline-delete", "no")),
                SeedProjectConfiguration.of("nemerosa/ontrack")
        );

        verify(launcher, times(1)).delete("ontrack/ontrack-feature-xxx/ontrack-feature-xxx-seed");
    }

    @Test
    public void post_delete_pipeline_not_deleted_project_config() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.DELETION
                ),
                launcher,
                new SeedConfiguration(Collections.singletonMap("pipeline-delete", "yes")),
                SeedProjectConfiguration.of(
                        ImmutableMap.of(
                                "id", "nemerosa/ontrack",
                                "pipeline-delete", "no"
                        )
                )
        );

        verify(launcher, times(1)).delete("ontrack/ontrack-feature-xxx/ontrack-feature-xxx-seed");
    }

    @Test
    public void post_seed_default() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.SEED
                ),
                launcher,
                new SeedConfiguration(Collections.<String, Object>emptyMap()),
                SeedProjectConfiguration.of("nemerosa/ontrack")
        );

        verify(launcher, times(1)).launch("ontrack/ontrack-feature-xxx/ontrack-feature-xxx-seed", null);
    }

    @Test
    public void post_seed_pipeline_not_auto_at_global_config() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.SEED
                ),
                launcher,
                new SeedConfiguration(Collections.singletonMap("pipeline-auto", "no")),
                SeedProjectConfiguration.of("nemerosa/ontrack")
        );

        verify(launcher, never()).launch(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void post_seed_pipeline_auto_at_project_config() {
        SeedBranchStrategy strategy = new SeedBranchStrategy();

        SeedLauncher launcher = mock(SeedLauncher.class);

        strategy.post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/xxx",
                        SeedEventType.SEED
                ),
                launcher,
                new SeedConfiguration(Collections.singletonMap("pipeline-auto", "no")),
                SeedProjectConfiguration.of(
                        ImmutableMap.of(
                                "id", "nemerosa/ontrack",
                                "pipeline-auto", "yes"
                        )
                )
        );

        verify(launcher, times(1)).launch("ontrack/ontrack-feature-xxx/ontrack-feature-xxx-seed", null);
    }

}

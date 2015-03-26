package net.nemerosa.seed.jenkins.connector.github;

import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

import static net.nemerosa.seed.jenkins.connector.EndPointTestSupport.mockStaplerResponse;
import static org.mockito.Mockito.*;

public class GitHubEndPointTest {

    @Test
    public void create_branch() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/ontrack",
                        "feature/123-test",
                        SeedEventType.CREATION,
                        SeedChannel.of("Seed GitHub end point"))
        );
    }
}

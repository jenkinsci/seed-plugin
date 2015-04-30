package net.nemerosa.seed.triggering.connector.github;

import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.SeedChannel;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedEventType;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static net.nemerosa.seed.triggering.connector.EndPointTestSupport.mockStaplerResponse;
import static org.mockito.Mockito.*;

public class GitHubEndPointTest {

    @Test
    public void create_branch() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockGitHubRequest("create", "/github-payload-create.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "test-4",
                        SeedEventType.CREATION,
                        SeedChannel.of("Seed GitHub end point"))
        );
    }

    @Test
    public void delete_branch() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockGitHubRequest("delete", "/github-payload-delete.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "test-4",
                        SeedEventType.DELETION,
                        SeedChannel.of("Seed GitHub end point"))
        );
    }

    @Test
    public void seed_event() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockGitHubRequest("push", "/github-payload-seed.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.SEED,
                        SeedChannel.of("Seed GitHub end point"))
        );
    }

    @Test
    public void commit_event_without_signature() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockGitHubRequest("push", "/github-payload-commit.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.COMMIT,
                        SeedChannel.of("Seed GitHub end point"))
                        .withParam("commit", "a10c3027a04ab066adc7a2a3d4735a7026fc1c59")
        );
    }

    @Test
    public void commit_event_with_signature() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockGitHubRequest("push", "/github-payload-commit-signed.json");
        when(request.getHeader("X-Hub-Signature")).thenReturn("sha1=0152b9c1b5171f7162fd98c45d81f37fdf03846c");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        when(seedService.getSecretKey("nemerosa/seed-demo", "github")).thenReturn("ABCDEF123456");
        // Call
        new GitHubEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.COMMIT,
                        SeedChannel.of("Seed GitHub end point"))
                        .withParam("commit", "a10c3027a04ab066adc7a2a3d4735a7026fc1c59")
        );
    }

    private StaplerRequest mockGitHubRequest(String event, String payload) throws IOException {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getHeader("X-GitHub-Event")).thenReturn(event);
        when(request.getReader()).thenReturn(
                new BufferedReader(
                        new InputStreamReader(
                                getClass().getResourceAsStream(payload),
                                "UTF-8"
                        )
                )
        );
        return request;
    }
}

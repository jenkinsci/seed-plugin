package net.nemerosa.seed.jenkins.connector.github;

import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedChannel;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static net.nemerosa.seed.jenkins.connector.EndPointTestSupport.mockStaplerResponse;
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
    public void commit_event() throws IOException {
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
                        .withParam("commit", "3e872d2dddac526ab5c6ea23226ac4db47735166")
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

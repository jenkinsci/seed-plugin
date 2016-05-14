package net.nemerosa.seed.triggering.connector.http;

import net.nemerosa.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.connector.UnknownRequestException;
import net.nemerosa.jenkins.seed.generator.MissingParameterException;
import net.nemerosa.seed.triggering.SeedChannel;
import net.nemerosa.seed.triggering.SeedEvent;
import net.nemerosa.seed.triggering.SeedEventType;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

import static net.nemerosa.seed.triggering.connector.EndPointTestSupport.mockStaplerResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class HttpEndPointTest {

    public static final SeedChannel HTTP_CHANNEL = SeedChannel.of("http", "Seed HTTP end point");
    private SeedService seedService;
    private HttpEndPoint endPoint;

    @Before
    public void before() {
        seedService = mock(SeedService.class);
        endPoint = new HttpEndPoint(seedService);
    }

    @Test(expected = UnknownRequestException.class)
    public void extractEvent_no_path() {
        StaplerRequest request = mock(StaplerRequest.class);
        endPoint.extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_all() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        endPoint.extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_branch() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        endPoint.extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_project() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("branch")).thenReturn("master");
        endPoint.extractEvent(request);
    }

    @Test
    public void extractEvent_create() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = endPoint.extractEvent(request);
        assertEquals(SeedEventType.CREATION, event.getType());
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
    }

    @Test
    public void extractEvent_delete() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/delete");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = endPoint.extractEvent(request);
        assertEquals(SeedEventType.DELETION, event.getType());
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
    }

    @Test
    public void extractEvent_seed() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/seed");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = endPoint.extractEvent(request);
        assertEquals(SeedEventType.SEED, event.getType());
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
    }

    @Test
    public void extractEvent_commit_no_parameter() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = endPoint.extractEvent(request);
        assertEquals(SeedEventType.COMMIT, event.getType());
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
        assertNull(event.getConfiguration().getString("commit", false, null));
    }

    @Test
    public void extractEvent_commit_parameter() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        when(request.getParameter("commit")).thenReturn("abcdef");
        SeedEvent event = endPoint.extractEvent(request);
        assertEquals(SeedEventType.COMMIT, event.getType());
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
        assertEquals("abcdef", event.getConfiguration().getString("commit"));
    }

    @Test
    public void commit_with_parameter() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        when(request.getParameter("commit")).thenReturn("abcdef");
        // Call
        endPoint.doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed",
                        "master",
                        SeedEventType.COMMIT,
                        HTTP_CHANNEL).withParam("commit", "abcdef")
        );
    }

    @Test
    public void commit_with_no_parameter() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        // Call
        endPoint.doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed",
                        "master",
                        SeedEventType.COMMIT,
                        HTTP_CHANNEL)
        );
    }

    @Test
    public void commit_token_missing() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        // Service mock
        when(seedService.getSecretKey("nemerosa/seed", "http")).thenReturn("ABCDEF123456");
        // Call
        endPoint.doDynamic(request, response);
        // Verifying
        verify(seedService, never()).post(any(SeedEvent.class));
    }

    @Test
    public void commit_token_mismatch() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        when(request.getHeader("X-Seed-Token")).thenReturn("123");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        endPoint.doDynamic(request, response);
        // Verifying
        verify(seedService, never()).post(any(SeedEvent.class));
    }

    @Test
    public void commit_authenticated() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        when(request.getHeader("X-Seed-Token")).thenReturn("ABCDEF123456");
        // Service mock
        when(seedService.getSecretKey("nemerosa/seed", "http")).thenReturn("ABCDEF123456");
        // Call
        endPoint.doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed",
                        "master",
                        SeedEventType.COMMIT,
                        HTTP_CHANNEL)
        );
    }

}

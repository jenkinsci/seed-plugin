package net.nemerosa.seed.jenkins.connector.http;

import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.connector.UnknownRequestException;
import net.nemerosa.seed.jenkins.model.MissingParameterException;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class HttpEndPointTest {

    @Test(expected = UnknownRequestException.class)
    public void extractEvent_no_path() {
        StaplerRequest request = mock(StaplerRequest.class);
        new HttpEndPoint().extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_all() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        new HttpEndPoint().extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_branch() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        new HttpEndPoint().extractEvent(request);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_project() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("branch")).thenReturn("master");
        new HttpEndPoint().extractEvent(request);
    }

    @Test
    public void extractEvent_create() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/create");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = new HttpEndPoint().extractEvent(request);
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
        SeedEvent event = new HttpEndPoint().extractEvent(request);
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
        SeedEvent event = new HttpEndPoint().extractEvent(request);
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
        SeedEvent event = new HttpEndPoint().extractEvent(request);
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
        SeedEvent event = new HttpEndPoint().extractEvent(request);
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
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new HttpEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed",
                        "master",
                        SeedEventType.COMMIT
                ).withParam("commit", "abcdef")
        );
    }

    private StaplerResponse mockStaplerResponse() throws IOException {
        StaplerResponse response = mock(StaplerResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(System.out));
        return response;
    }

    @Test
    public void commit_with_no_parameter() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getRestOfPath()).thenReturn("/commit");
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        new HttpEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed",
                        "master",
                        SeedEventType.COMMIT
                )
        );
    }

}

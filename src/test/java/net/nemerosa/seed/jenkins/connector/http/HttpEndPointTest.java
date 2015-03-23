package net.nemerosa.seed.jenkins.connector.http;

import net.nemerosa.seed.jenkins.SeedService;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.model.MissingParameterException;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class HttpEndPointTest {

    @Test
    public void extractParameter_ok() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        assertEquals(
                "nemerosa/seed",
                new HttpEndPoint().extractParameter(request, "project")
        );
    }

    @Test(expected = MissingParameterException.class)
    public void extractParameter_nok() {
        StaplerRequest request = mock(StaplerRequest.class);
        new HttpEndPoint().extractParameter(request, "project");
    }

    @Test(expected = MissingParameterException.class)
    public void extractParameter_required() {
        StaplerRequest request = mock(StaplerRequest.class);
        new HttpEndPoint().extractParameter(request, "project", true);
    }

    @Test
    public void extractParameter_not_required() {
        StaplerRequest request = mock(StaplerRequest.class);
        assertNull(new HttpEndPoint().extractParameter(request, "project", false));
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_all() {
        StaplerRequest request = mock(StaplerRequest.class);
        new HttpEndPoint().extractEvent(request, SeedEventType.CREATION);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_branch() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        new HttpEndPoint().extractEvent(request, SeedEventType.CREATION);
    }

    @Test(expected = MissingParameterException.class)
    public void extractEvent_missing_project() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getParameter("branch")).thenReturn("master");
        new HttpEndPoint().extractEvent(request, SeedEventType.CREATION);
    }

    @Test
    public void extractEvent() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        when(request.getParameter("branch")).thenReturn("master");
        SeedEvent event = new HttpEndPoint().extractEvent(request, SeedEventType.CREATION);
        assertEquals("nemerosa/seed", event.getProject());
        assertEquals("master", event.getBranch());
    }

    @Test
    public void commit_with_parameter() throws IOException {
        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);
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

    @Test
    public void commit_with_no_parameter() throws IOException {
        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);
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

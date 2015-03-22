package net.nemerosa.seed.jenkins.connector.http;

import net.nemerosa.seed.jenkins.connector.http.HttpEndPoint;
import net.nemerosa.seed.jenkins.model.SeedEvent;
import net.nemerosa.seed.jenkins.model.SeedEventType;
import net.nemerosa.seed.jenkins.support.MissingParameterException;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

}

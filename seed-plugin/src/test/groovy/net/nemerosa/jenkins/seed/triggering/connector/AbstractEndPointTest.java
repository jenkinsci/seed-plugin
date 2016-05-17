package net.nemerosa.jenkins.seed.triggering.connector;

import net.nemerosa.jenkins.seed.generator.MissingParameterException;
import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractEndPointTest {

    @Test
    public void extractParameter_ok() {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getParameter("project")).thenReturn("nemerosa/seed");
        Assert.assertEquals(
                "nemerosa/seed",
                AbstractEndPoint.extractParameter(request, "project")
        );
    }

    @Test(expected = MissingParameterException.class)
    public void extractParameter_nok() {
        StaplerRequest request = mock(StaplerRequest.class);
        AbstractEndPoint.extractParameter(request, "project");
    }

    @Test(expected = MissingParameterException.class)
    public void extractParameter_required() {
        StaplerRequest request = mock(StaplerRequest.class);
        AbstractEndPoint.extractParameter(request, "project", true);
    }

    @Test
    public void extractParameter_not_required() {
        StaplerRequest request = mock(StaplerRequest.class);
        assertNull(AbstractEndPoint.extractParameter(request, "project", false));
    }

}

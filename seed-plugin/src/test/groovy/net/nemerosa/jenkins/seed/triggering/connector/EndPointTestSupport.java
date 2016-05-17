package net.nemerosa.jenkins.seed.triggering.connector;

import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EndPointTestSupport {

    public static StaplerResponse mockStaplerResponse() throws IOException {
        StaplerResponse response = mock(StaplerResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(System.out));
        return response;
    }

}

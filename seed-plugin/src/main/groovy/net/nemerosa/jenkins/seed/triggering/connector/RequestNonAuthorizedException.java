package net.nemerosa.jenkins.seed.triggering.connector;

import net.nemerosa.jenkins.seed.SeedException;

public class RequestNonAuthorizedException extends SeedException {
    public RequestNonAuthorizedException() {
        super("Request non authorized");
    }
}

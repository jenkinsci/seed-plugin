package net.nemerosa.jenkins.seed.triggering.connector;

import net.nemerosa.seed.config.SeedException;

public class RequestNonAuthorizedException extends SeedException {
    public RequestNonAuthorizedException() {
        super("Request non authorized");
    }
}

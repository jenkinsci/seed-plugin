package net.nemerosa.seed.jenkins.connector;

import net.nemerosa.seed.jenkins.model.SeedException;

public class RequestNonAuthorizedException extends SeedException {
    public RequestNonAuthorizedException() {
        super("Request non authorized");
    }
}

package net.nemerosa.seed.jenkins.connector;

import net.nemerosa.seed.jenkins.model.SeedException;

public class CannotHandleRequestException extends SeedException {
    public CannotHandleRequestException(Exception ex) {
        super(ex, "Request cannot be handled");
    }
}

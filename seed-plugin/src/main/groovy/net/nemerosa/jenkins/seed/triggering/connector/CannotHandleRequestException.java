package net.nemerosa.jenkins.seed.triggering.connector;

import net.nemerosa.jenkins.seed.SeedException;

public class CannotHandleRequestException extends SeedException {
    public CannotHandleRequestException(Exception ex) {
        super(ex, "Request cannot be handled");
    }
}

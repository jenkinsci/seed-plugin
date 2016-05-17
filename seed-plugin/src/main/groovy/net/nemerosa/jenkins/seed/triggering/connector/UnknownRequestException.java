package net.nemerosa.jenkins.seed.triggering.connector;

public class UnknownRequestException extends RuntimeException {
    public UnknownRequestException(String message) {
        super(message);
    }
}

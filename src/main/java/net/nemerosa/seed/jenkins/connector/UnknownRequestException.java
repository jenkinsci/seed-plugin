package net.nemerosa.seed.jenkins.connector;

public class UnknownRequestException extends RuntimeException {
    public UnknownRequestException(String message) {
        super(message);
    }
}

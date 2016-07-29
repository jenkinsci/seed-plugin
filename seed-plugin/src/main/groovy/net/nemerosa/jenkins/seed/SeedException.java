package net.nemerosa.jenkins.seed;

public abstract class SeedException extends RuntimeException {

    public SeedException(String message, Object... parameters) {
        super(String.format(message, parameters));
    }

    public SeedException(Exception cause, String message, Object... parameters) {
        super(String.format(message, parameters), cause);
    }

}

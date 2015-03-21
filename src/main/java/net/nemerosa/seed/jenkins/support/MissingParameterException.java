package net.nemerosa.seed.jenkins.support;

public class MissingParameterException extends RuntimeException {
    public MissingParameterException(String name) {
        super(String.format("Missing request parameter: %s", name));
    }
}

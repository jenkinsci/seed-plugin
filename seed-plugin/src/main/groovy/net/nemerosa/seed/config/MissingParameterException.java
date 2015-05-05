package net.nemerosa.seed.config;

public class MissingParameterException extends SeedException {
    public MissingParameterException(String name) {
        super("Missing request parameter: %s", name);
    }
}

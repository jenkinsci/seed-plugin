package net.nemerosa.seed.jenkins.model;

import net.nemerosa.seed.jenkins.model.SeedException;

public class MissingParameterException extends SeedException {
    public MissingParameterException(String name) {
        super("Missing request parameter: %s", name);
    }
}

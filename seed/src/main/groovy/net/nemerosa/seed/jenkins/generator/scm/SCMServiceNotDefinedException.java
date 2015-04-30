package net.nemerosa.seed.jenkins.generator.scm;

import net.nemerosa.seed.config.SeedException;

public class SCMServiceNotDefinedException extends SeedException {
    public SCMServiceNotDefinedException(String type) {
        super("Cannot find SCM service for type: %s", type);
    }
}

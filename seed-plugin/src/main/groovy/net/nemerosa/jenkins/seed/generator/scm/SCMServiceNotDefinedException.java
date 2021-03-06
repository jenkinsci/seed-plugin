package net.nemerosa.jenkins.seed.generator.scm;

import net.nemerosa.jenkins.seed.SeedException;

public class SCMServiceNotDefinedException extends SeedException {
    public SCMServiceNotDefinedException(String type) {
        super("Cannot find SCM service for type: %s", type);
    }
}

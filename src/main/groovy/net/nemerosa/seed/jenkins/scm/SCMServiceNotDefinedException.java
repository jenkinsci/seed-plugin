package net.nemerosa.seed.jenkins.scm;

import net.nemerosa.seed.jenkins.model.SeedException;

public class SCMServiceNotDefinedException extends SeedException {
    public SCMServiceNotDefinedException(String type) {
        super("Cannot find SCM service for type: %s", type);
    }
}

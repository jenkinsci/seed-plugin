package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedException;

public class JobNotParameterizedException extends SeedException {
    public JobNotParameterizedException(String name) {
        super("Job %s is not parameterized.", name);
    }
}

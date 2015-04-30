package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedException;

public class JobNotParameterizedException extends SeedException {
    public JobNotParameterizedException(String name) {
        super("Job %s is not parameterized.", name);
    }
}

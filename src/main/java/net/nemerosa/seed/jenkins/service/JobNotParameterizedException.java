package net.nemerosa.seed.jenkins.service;

public class JobNotParameterizedException extends RuntimeException {
    public JobNotParameterizedException(String name) {
        super(String.format("Job %s is not parameterized.", name));
    }
}

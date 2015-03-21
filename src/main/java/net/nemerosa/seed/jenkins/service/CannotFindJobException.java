package net.nemerosa.seed.jenkins.service;

public class CannotFindJobException extends RuntimeException {
    public CannotFindJobException(String context, String path) {
        super(String.format("Cannot find job in path %s with name %s", context, path));
    }
}

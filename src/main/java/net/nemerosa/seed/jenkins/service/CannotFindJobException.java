package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedException;

public class CannotFindJobException extends SeedException {
    public CannotFindJobException(String context, String path) {
        super("Cannot find job in path %s with name %s", context, path);
    }
}

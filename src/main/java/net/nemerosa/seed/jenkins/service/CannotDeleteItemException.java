package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedException;

public class CannotDeleteItemException extends SeedException {
    public CannotDeleteItemException(String path, Exception e) {
        super(
                e,
                "Cannot delete item at %s",
                path
        );
    }
}

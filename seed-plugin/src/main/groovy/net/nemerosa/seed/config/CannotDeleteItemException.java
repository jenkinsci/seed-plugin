package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedException;

public class CannotDeleteItemException extends SeedException {
    public CannotDeleteItemException(String path, Exception e) {
        super(
                e,
                "Cannot delete item at %s",
                path
        );
    }
}

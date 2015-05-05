package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedException;

public class CannotFindJobException extends SeedException {
    public CannotFindJobException(String context, String path) {
        super("Cannot find job in path %s with name %s", context, path);
    }
}

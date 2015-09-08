package net.nemerosa.seed.config;

public class CannotFindJobException extends SeedException {
    public CannotFindJobException(String context, String path) {
        super("Cannot find job in context [%s] with path [%s]", context, path);
    }
}

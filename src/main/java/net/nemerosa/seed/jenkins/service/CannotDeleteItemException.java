package net.nemerosa.seed.jenkins.service;

public class CannotDeleteItemException extends RuntimeException {
    public CannotDeleteItemException(String path, Exception e) {
        super(
                String.format(
                        "Cannot delete item at %s",
                        path
                ),
                e
        );
    }
}

package net.nemerosa.jenkins.seed;

public class CannotDeleteItemException extends SeedException {
    public CannotDeleteItemException(String path, Exception e) {
        super(
                e,
                "Cannot delete item at %s",
                path
        );
    }
}

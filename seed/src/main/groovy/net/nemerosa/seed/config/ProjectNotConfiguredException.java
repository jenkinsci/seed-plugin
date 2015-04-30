package net.nemerosa.seed.config;

public class ProjectNotConfiguredException extends SeedException {
    public ProjectNotConfiguredException(String id) {
        super("Project %s is not configured and auto configuration is not enabled.", id);
    }
}

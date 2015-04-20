package net.nemerosa.seed.jenkins.model;

public class ProjectNotConfiguredException extends SeedException {
    public ProjectNotConfiguredException(String id) {
        super("Project %s is not configured and auto configuration is not enabled.", id);
    }
}

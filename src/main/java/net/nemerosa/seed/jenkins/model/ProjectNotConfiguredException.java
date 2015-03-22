package net.nemerosa.seed.jenkins.model;

public class ProjectNotConfiguredException extends RuntimeException {
    public ProjectNotConfiguredException(String id) {
        super(String.format("Project %s is not configured and auto configuration is not enabled.", id));
    }
}

package net.nemerosa.seed.jenkins.model;

public class SeedEvent {

    private final String project;
    private final String branch;
    private final SeedEventType type;

    public SeedEvent(String project, String branch, SeedEventType type) {
        this.project = project;
        this.branch = branch;
        this.type = type;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public SeedEventType getType() {
        return type;
    }

}

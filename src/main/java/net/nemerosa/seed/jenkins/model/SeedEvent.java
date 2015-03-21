package net.nemerosa.seed.jenkins.model;

public class SeedEvent {

    private final String project;
    private final String branch;

    protected SeedEvent(String project, String branch) {
        this.project = project;
        this.branch = branch;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

}

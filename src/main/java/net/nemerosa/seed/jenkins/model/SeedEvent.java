package net.nemerosa.seed.jenkins.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeedEvent {

    private final String project;
    private final String branch;
    private final SeedEventType type;
    private final Map<String, Object> parameters = new LinkedHashMap<String, Object>();

    public SeedEvent(String project, String branch, SeedEventType type) {
        this.project = project;
        this.branch = branch;
        this.type = type;
    }

    public SeedEvent withParam(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Configuration getConfiguration() {
        return new Configuration(parameters);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedEvent event = (SeedEvent) o;

        return branch.equals(event.branch) &&
                parameters.equals(event.parameters)
                && project.equals(event.project)
                && type == event.type;
    }

    @Override
    public int hashCode() {
        int result = project.hashCode();
        result = 31 * result + branch.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }
}

package net.nemerosa.seed.jenkins.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public class SeedConfiguration {

    private final Map<String, SeedProjectConfiguration> projects;

    public SeedConfiguration(Collection<SeedProjectConfiguration> projects) {
        this.projects = Maps.uniqueIndex(
                projects,
                new Function<SeedProjectConfiguration, String>() {
                    @Override
                    public String apply(SeedProjectConfiguration input) {
                        return input.getId();
                    }
                }
        );
    }

    public String getProjectSeed(String project) {
        // Gets the configuration for a project
        SeedProjectConfiguration configuration = getProjectConfiguration(project);
        // OK
        return configuration.getSeed();
    }

    public String getBranchSeed(String project, String branch) {
        // Gets the configuration for the project
        SeedProjectConfiguration configuration = getProjectConfiguration(project);
        // OK
        return configuration.getBranchSeed(branch);
    }

    public SeedProjectConfiguration getProjectConfiguration(String id) {
        SeedProjectConfiguration configuration = projects.get(id);
        if (configuration != null) {
            return configuration;
        } else {
            return SeedProjectConfiguration.of(id);
        }
    }
}

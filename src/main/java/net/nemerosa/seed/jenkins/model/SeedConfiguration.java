package net.nemerosa.seed.jenkins.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SeedConfiguration {

    private final boolean autoConfigure;
    private final Map<String, SeedProjectConfiguration> projects;

    public SeedConfiguration(Collection<SeedProjectConfiguration> projects, boolean autoConfigure) {
        this.autoConfigure = autoConfigure;
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

    public boolean isAutoConfigure() {
        return autoConfigure;
    }

    public SeedProjectConfiguration getProjectConfiguration(String id) {
        SeedProjectConfiguration configuration = projects.get(id);
        if (configuration != null) {
            return configuration;
        } else if (autoConfigure) {
            return SeedProjectConfiguration.of(id);
        } else {
            throw new ProjectNotConfiguredException(id);
        }
    }

    public static SeedConfiguration parseYaml(String text) {
        Yaml yaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, ?> result = (Map<String, ?>) yaml.load(text);
        // Parsing
        return parseMap(result);
    }

    public static SeedConfiguration parseMap(Map<String, ?> map) {
        // TODO Global configuration
        boolean autoConfigure = new Configuration(map).getBoolean("auto-configure", false, true);
        // Projects
        List<SeedProjectConfiguration> parsedProjects = new ArrayList<SeedProjectConfiguration>();
        @SuppressWarnings("unchecked")
        Collection<Map<String, ?>> projects = (Collection<Map<String, ?>>) map.get("projects");
        if (projects != null) {
            for (Map<String, ?> projectMap : projects) {
                parsedProjects.add(
                        new SeedProjectConfiguration(projectMap)
                );
            }
        }
        // OK
        return new SeedConfiguration(
                parsedProjects,
                autoConfigure);
    }

}

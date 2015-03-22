package net.nemerosa.seed.jenkins.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import net.nemerosa.seed.jenkins.support.MissingParameterException;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class SeedConfiguration extends Configuration {

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
        // Global configuration
        boolean autoConfigure = getBoolean(map, "auto-configure", false, true);
        // Projects
        List<SeedProjectConfiguration> parsedProjects = new ArrayList<SeedProjectConfiguration>();
        @SuppressWarnings("unchecked")
        Collection<Map<String, ?>> projects = (Collection<Map<String, ?>>) map.get("projects");
        if (projects != null) {
            for (Map<String, ?> projectMap : projects) {
                // Default configuration
                String id = get(projectMap, "id");
                SeedProjectConfiguration defaultConfiguration = SeedProjectConfiguration.of(id);
                String name = get(projectMap, "name", defaultConfiguration.getName());
                String seed = get(projectMap, "seed", defaultConfiguration.getSeed());
                String branchSeed = get(projectMap, "branchSeed", defaultConfiguration.getBranchSeed());
                String branchStart = get(projectMap, "branchStart", defaultConfiguration.getBranchStart());
                parsedProjects.add(
                        new SeedProjectConfiguration(
                                id,
                                name,
                                seed,
                                branchSeed,
                                branchStart
                        )
                );
            }
        }
        // OK
        return new SeedConfiguration(
                parsedProjects,
                autoConfigure);
    }

    private static boolean getBoolean(Map<String, ?> map, String name, boolean required, boolean defaultValue) {
        String value = get(map, name, required, null);
        if (value == null) {
            return defaultValue;
        } else {
            return "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
        }
    }

    private static String get(Map<String, ?> map, String name, String defaultValue) {
        return get(map, name, false, defaultValue);
    }

    private static String get(Map<String, ?> map, String name, boolean required, String defaultValue) {
        String value = Objects.toString(map.get(name), null);
        if (value == null) {
            if (required) {
                throw new MissingParameterException(name);
            } else {
                return defaultValue;
            }
        } else {
            return value;
        }
    }

    public static String get(Map<String, ?> map, String name) {
        return get(map, name, true, null);
    }
}

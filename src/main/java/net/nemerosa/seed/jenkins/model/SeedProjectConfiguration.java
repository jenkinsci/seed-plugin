package net.nemerosa.seed.jenkins.model;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Map;

public class SeedProjectConfiguration extends Configuration {

    private final String id;
    private final String name;

    public SeedProjectConfiguration(Map<String, ?> data) {
        super(data);
        this.id = getString("id");
        this.name = getString("name", false, defaultName(id));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBranchStrategy() {
        return getString("branch-strategy", false, "seed");
    }

    public static SeedProjectConfiguration of(String id) {
        return of(Collections.singletonMap("id", id));
    }

    public static SeedProjectConfiguration of(Map<String, ?> data) {
        return new SeedProjectConfiguration(data);
    }

    public static String defaultName(String id) {
        if (StringUtils.contains(id, "/")) {
            return normalise(StringUtils.substringAfterLast(id, "/"));
        } else {
            return normalise(id);
        }
    }

}

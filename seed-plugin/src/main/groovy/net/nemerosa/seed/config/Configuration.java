package net.nemerosa.seed.config;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class Configuration {

    public static String normalise(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "-");
    }

    private final Map<String, ?> data;

    public Configuration(Map<String, ?> data) {
        this.data = data;
    }

    protected Map<String, ?> mergeData(Configuration cfg) {
        Map<String, Object> result = new HashMap<String, Object>(data);
        result.putAll(cfg.data);
        return result;
    }

    public String getString(String name) {
        return getString(name, true, null);
    }

    public String getString(String name, boolean required, String defaultValue) {
        if (name.contains(".")) {
            String prefix = StringUtils.substringBefore(name, ".");
            String rest = StringUtils.substringAfter(name, ".");
            @SuppressWarnings("unchecked")
            Map<String, ?> child = (Map<String, ?>) data.get(prefix);
            return new Configuration(child).getString(rest, required, defaultValue);
        } else {
            String value = Objects.toString(data.get(name), null);
            if (value != null) {
                return value;
            } else if (required) {
                throw new MissingParameterException(name);
            } else {
                return defaultValue;
            }
        }
    }

    public boolean getBoolean(String name, boolean required, boolean defaultValue) {
        return toBoolean(getString(name, required, null), defaultValue);
    }

    public List<Map<String, ?>> getList(String name) {
        @SuppressWarnings("unchecked")
        List<Map<String, ?>> list = (List<Map<String, ?>>) data.get(name);
        if (list != null) {
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> getListString(String name) {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) data.get(name);
        if (list != null) {
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    private static boolean toBoolean(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
        }
    }

    public static boolean getBoolean(String name, Configuration configuration, Configuration globalConfiguration, boolean defaultValue) {
        return toBoolean(
                getValue(name, configuration, globalConfiguration, null),
                defaultValue
        );
    }

    public static String getValue(String name, Configuration configuration, Configuration globalConfiguration, String defaultValue) {
        return configuration.getString(
                name,
                false,
                globalConfiguration.getString(
                        name,
                        false,
                        defaultValue
                )
        );
    }

    public static List<String> getList(String name, Configuration configuration, Configuration globalConfiguration) {
        List<String> list = configuration.getListString(name);
        if (list != null && !list.isEmpty()) {
            return list;
        } else {
            return globalConfiguration.getListString(name);
        }
    }
}

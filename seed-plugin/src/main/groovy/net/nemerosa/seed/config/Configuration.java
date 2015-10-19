package net.nemerosa.seed.config;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
        return mergeMap(cfg.data, this.data);
    }

    public Map<String, ?> getData() {
        return data;
    }

    private static Map<String, ?> mergeMap(Map<String, ?> data, Map<String, ?> defaultData) {
        Map<String, Object> result = new LinkedHashMap<String, Object>(data);
        for (Map.Entry<String, ?> defaultEntry : defaultData.entrySet()) {
            String name = defaultEntry.getKey();
            Object defaultValue = defaultEntry.getValue();
            Object dataValue = data.get(name);
            if (dataValue != null) {
                if (dataValue instanceof Map && defaultValue instanceof Map) {
                    // Recursive merge
                    //noinspection unchecked
                    result.put(
                            name,
                            mergeMap(
                                    (Map<String, ?>) dataValue,
                                    (Map<String, ?>) defaultValue
                            )
                    );
                }
            } else {
                result.put(name, defaultValue);
            }
        }
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

    public Map<String, String> getParameters(String key) {
        Object map = data.get(key);
        if (map instanceof Map) {
            //noinspection unchecked
            return (Map<String, String>) map;
        } else {
            return Collections.emptyMap();
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

    public static String getFieldInList(String listName, Configuration configuration, Configuration globalConfiguration, String idField, String idValue, String valueField) {
        String value = getFieldInList(listName, configuration, idField, idValue, valueField);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else {
            return getFieldInList(listName, globalConfiguration, idField, idValue, valueField);
        }
    }

    protected static String getFieldInList(String listName, Configuration configuration, final String idField, final String idValue, String valueField) {
        List<Map<String, ?>> items = configuration.getList(listName);
        Map<String, ?> itemData = Iterables.find(
                items,
                new Predicate<Map<String, ?>>() {
                    @Override
                    public boolean apply(Map<String, ?> data) {
                        String itemId = Objects.toString(data.get(idField), null);
                        return StringUtils.equals(idValue, itemId);
                    }
                },
                null
        );
        if (itemData != null) {
            return Objects.toString(
                    itemData.get(valueField),
                    null
            );
        } else {
            return null;
        }
    }

    public static Map<String, String> getParameters(String key, Configuration configuration, Configuration globalConfiguration) {
        Map<String, String> parameters = configuration.getParameters(key);
        Map<String, String> globalParameters = globalConfiguration.getParameters(key);
        Map<String, String> result = new TreeMap<String, String>(globalParameters);
        result.putAll(parameters); // Global overridden by configuration
        return result;
    }
}

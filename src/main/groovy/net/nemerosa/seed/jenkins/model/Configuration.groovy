package net.nemerosa.seed.jenkins.model

import net.nemerosa.seed.jenkins.support.MissingParameterException
import org.apache.commons.lang.StringUtils

class Configuration {

    static String normalise(String value) {
        return value.replaceAll(/[^A-Za-z0-9._-]/, "-")
    }

    private final Map<String, ?> data

    Configuration(Map<String, ?> data) {
        this.data = data
    }

    String getString(String name, boolean required = true, String defaultValue = null) {
        if (name.contains('.')) {
            String prefix = StringUtils.substringBefore(name, '.')
            String rest = StringUtils.substringAfter(name, '.')
            Map child = data[prefix] as Map
            return new Configuration(child).getString(rest, required, defaultValue)
        } else {
            String value = data[name] as String
            if (value) {
                return value
            } else if (required) {
                throw new MissingParameterException(name)
            } else {
                return defaultValue
            }
        }
    }

    boolean getBoolean(String name, boolean required, boolean defaultValue) {
        String value = getString(name, required)
        if (value == null) {
            return defaultValue
        } else {
            return "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)
        }
    }

    List<Map<String, ?>> getList(String name) {
        return data[name] as List ?: []
    }
}

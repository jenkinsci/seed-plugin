package net.nemerosa.jenkins.seed.support;

import org.apache.commons.lang.StringUtils;

public class Evaluator {

    public static String evaluate(String expression, String name, String value) {
        return expression
                .replace(lower(name), value.toLowerCase())
                .replace(upper(name), value.toUpperCase())
                .replace(upper_underscore(name), value.toUpperCase().replace("-", "_"))
                .replace(capitalize(name), StringUtils.capitalize(value))
                ;
    }

    private static String lower(String name) {
        return "${" + name.toLowerCase() + "}";
    }

    private static String upper(String name) {
        return "${" + name.toUpperCase() + "}";
    }

    private static String upper_underscore(String name) {
        return "${" + name.toUpperCase() + "_}";
    }

    private static String capitalize(String name) {
        return "${" + StringUtils.capitalize(name) + "}";
    }

}

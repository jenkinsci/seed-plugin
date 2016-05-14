package net.nemerosa.jenkins.seed.support;

import org.apache.commons.lang.StringUtils;

public class Evaluator {

    public static String evaluate(String expression, String defaultExpression, String name, String value) {
        String actualExpression = getActualExpression(expression, defaultExpression);
        return evaluate(actualExpression, name, value);
    }

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

    private static String getActualExpression(String expression, String defaultExpression) {
        String actualExpression;
        if (StringUtils.isNotBlank(expression)) {
            actualExpression = expression;
        } else {
            actualExpression = defaultExpression;
        }
        return actualExpression;
    }

}

package net.nemerosa.jenkins.seed.support;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Evaluator {

    public static String evaluate(String expression, String defaultExpression, Map<String, String> params) {
        String actualExpression = getActualExpression(expression, defaultExpression);
        return evaluate(actualExpression, params);
    }

    public static String evaluate(String expression, String defaultExpression, String name, String value) {
        String actualExpression = getActualExpression(expression, defaultExpression);
        return evaluate(actualExpression, name, value);
    }

    public static String evaluate(String expression, Map<String, String> params) {
        AtomicReference<String> s = new AtomicReference<>(expression);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            s.set(evaluate(s.get(), entry.getKey(), entry.getValue()));
        }
        return s.get();
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

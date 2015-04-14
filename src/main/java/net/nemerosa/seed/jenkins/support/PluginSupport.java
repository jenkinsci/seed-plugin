package net.nemerosa.seed.jenkins.support;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginSupport {

    public static final String REGEX_ENV_VARIABLE = "\\$\\{([a-zA-Z0-9_]+)\\}";

    public static String expand(String template, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        if (StringUtils.isBlank(template)) {
            return template;
        } else {
            Pattern pattern = Pattern.compile(REGEX_ENV_VARIABLE);
            Matcher matcher = pattern.matcher(template);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String name = matcher.group(1);
                String value = getParameter(name, theBuild, listener);
                if (value == null) {
                    throw new IllegalStateException("Cannot find any replacement value for environment variable " + name);
                }
                matcher = matcher.appendReplacement(result, value);
            }
            matcher.appendTail(result);
            return result.toString();
        }
    }

    public static String getParameter(String name, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        String value = theBuild.getBuildVariableResolver().resolve(name);
        if (value != null) {
            return value;
        } else {
            try {
                return theBuild.getEnvironment(listener).get(name);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot get value for " + name, ex);
            }
        }
    }
}

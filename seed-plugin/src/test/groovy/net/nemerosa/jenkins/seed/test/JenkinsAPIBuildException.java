package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

public class JenkinsAPIBuildException extends SeedException {
    public JenkinsAPIBuildException(String path, String message) {
        super("Problem when firing the build at %s: %s", path, message);
    }
}

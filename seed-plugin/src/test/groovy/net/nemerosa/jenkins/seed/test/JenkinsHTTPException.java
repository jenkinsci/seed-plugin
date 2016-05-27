package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

public class JenkinsHTTPException extends SeedException {

    public JenkinsHTTPException(String path, int code) {
        super("HTTP error (%d): %s", code, path);
    }
}

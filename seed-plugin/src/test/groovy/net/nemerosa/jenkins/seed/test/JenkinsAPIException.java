package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

import java.net.URL;

@Deprecated
public class JenkinsAPIException extends SeedException {
    public JenkinsAPIException(URL url, String message) {
        super("Error while calling %s: %s", url, message);
    }
}

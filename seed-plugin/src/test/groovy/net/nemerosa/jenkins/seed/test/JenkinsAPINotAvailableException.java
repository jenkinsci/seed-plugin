package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

import java.net.URL;

@Deprecated
public class JenkinsAPINotAvailableException extends SeedException {
    public JenkinsAPINotAvailableException(URL url) {
        super("Jenkins at %s cannot be reached.", url);
    }
}

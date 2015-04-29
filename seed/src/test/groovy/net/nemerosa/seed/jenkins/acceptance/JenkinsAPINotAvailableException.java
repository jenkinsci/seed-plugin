package net.nemerosa.seed.jenkins.acceptance;

import net.nemerosa.seed.jenkins.model.SeedException;

import java.net.URL;

public class JenkinsAPINotAvailableException extends SeedException {
    public JenkinsAPINotAvailableException(URL url) {
        super("Jenkins at %s cannot be reached.", url);
    }
}

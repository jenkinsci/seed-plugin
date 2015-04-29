package net.nemerosa.seed.jenkins.acceptance;

import net.nemerosa.seed.jenkins.model.SeedException;

import java.net.URL;

public class JenkinsAPINotFoundException extends SeedException {
    public JenkinsAPINotFoundException(URL url) {
        super("Jenkins at %s cannot be found.", url);
    }
}

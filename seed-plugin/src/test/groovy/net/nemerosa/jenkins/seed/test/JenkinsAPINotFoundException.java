package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

import java.net.URL;

@Deprecated
public class JenkinsAPINotFoundException extends SeedException {

    @Deprecated
    public JenkinsAPINotFoundException(URL url) {
        super("Jenkins at %s cannot be found.", url);
    }

    public JenkinsAPINotFoundException(String path) {
        super("Jenkins at %s cannot be found.", path);
    }
}

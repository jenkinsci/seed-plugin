package net.nemerosa.seed.acceptance;

import net.nemerosa.seed.config.SeedException;

import java.net.URL;

public class JenkinsAPIException extends SeedException {
    public JenkinsAPIException(URL url, String message) {
        super("Error while calling %s: %s", url, message);
    }
}

package net.nemerosa.seed.acceptance;

import net.nemerosa.seed.config.SeedException;

public class JenkinsAPIFoundException extends SeedException {
    public JenkinsAPIFoundException(String path) {
        super("Jenkins job at %s is present when it should have been gone.", path);
    }
}

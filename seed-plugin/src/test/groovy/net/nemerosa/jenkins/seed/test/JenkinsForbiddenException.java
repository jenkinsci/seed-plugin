package net.nemerosa.jenkins.seed.test;

import net.nemerosa.jenkins.seed.SeedException;

public class JenkinsForbiddenException extends SeedException {
    public JenkinsForbiddenException(String path) {
        super("Forbidden access (403): %s", path);
    }
}

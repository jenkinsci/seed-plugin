package net.nemerosa.jenkins.seed.test;

import net.nemerosa.seed.config.SeedException;

public class JenkinsAPIRefusedException extends SeedException {
    public JenkinsAPIRefusedException() {
        super("Request refused");
    }
}

package net.nemerosa.seed.acceptance;

import net.nemerosa.seed.config.SeedException;

public class JenkinsAPIRefusedException extends SeedException {
    public JenkinsAPIRefusedException() {
        super("Request refused");
    }
}

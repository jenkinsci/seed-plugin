package net.nemerosa.jenkins.seed.triggering;

import hudson.model.Cause;

public class SeedCause extends Cause {

    private final SeedChannel channel;

    public SeedCause(SeedChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getShortDescription() {
        return channel.getName();
    }
}

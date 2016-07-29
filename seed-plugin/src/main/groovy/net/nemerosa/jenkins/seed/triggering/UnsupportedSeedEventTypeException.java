package net.nemerosa.jenkins.seed.triggering;

import net.nemerosa.jenkins.seed.SeedException;

public class UnsupportedSeedEventTypeException extends SeedException {
    public UnsupportedSeedEventTypeException(SeedEventType type) {
        super("Unsupported event type: %s", type);
    }
}

package net.nemerosa.jenkins.seed.triggering;

import net.nemerosa.jenkins.seed.triggering.SeedEventType;
import net.nemerosa.seed.config.SeedException;

public class UnsupportedSeedEventTypeException extends SeedException {
    public UnsupportedSeedEventTypeException(SeedEventType type) {
        super("Unsupported event type: %s", type);
    }
}

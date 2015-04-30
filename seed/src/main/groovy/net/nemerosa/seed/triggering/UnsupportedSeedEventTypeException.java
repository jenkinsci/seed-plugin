package net.nemerosa.seed.triggering;

import net.nemerosa.seed.config.SeedException;

public class UnsupportedSeedEventTypeException extends SeedException {
    public UnsupportedSeedEventTypeException(SeedEventType type) {
        super("Unsupported event type: %s", type);
    }
}

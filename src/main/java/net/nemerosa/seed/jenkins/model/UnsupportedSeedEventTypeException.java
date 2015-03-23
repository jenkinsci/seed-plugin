package net.nemerosa.seed.jenkins.model;

public class UnsupportedSeedEventTypeException extends SeedException {
    public UnsupportedSeedEventTypeException(SeedEventType type) {
        super("Unsupported event type: %s", type);
    }
}

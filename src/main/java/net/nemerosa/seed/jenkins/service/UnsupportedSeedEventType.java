package net.nemerosa.seed.jenkins.service;

import net.nemerosa.seed.jenkins.model.SeedEventType;

public class UnsupportedSeedEventType extends RuntimeException {
    public UnsupportedSeedEventType(SeedEventType type) {
        super(String.format("Unsupported event type: %s", type));
    }
}

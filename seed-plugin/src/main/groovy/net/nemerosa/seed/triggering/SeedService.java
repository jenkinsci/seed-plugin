package net.nemerosa.seed.triggering;

import net.nemerosa.seed.triggering.SeedEvent;

public interface SeedService {

    void post(SeedEvent event);

    /**
     * Gets the signature key for a given project
     */
    String getSecretKey(String project, String context);
}

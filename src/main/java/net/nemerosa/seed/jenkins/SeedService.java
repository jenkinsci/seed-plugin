package net.nemerosa.seed.jenkins;

import net.nemerosa.seed.jenkins.model.SeedEvent;

public interface SeedService {

    void post(SeedEvent event);

    /**
     * Gets the signature key for a given project
     */
    String getSecretKey(String project, String context);
}

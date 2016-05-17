package net.nemerosa.jenkins.seed.triggering;

public interface SeedService {

    void post(SeedEvent event);

    /**
     * Gets the signature key for a given project
     */
    String getSecretKey(String project, String context);
}

package net.nemerosa.jenkins.seed.triggering;

import java.util.Map;

public interface SeedLauncher {

    void launch(SeedChannel channel, String path, Map<String, String> parameters);

    /**
     * Deletes the item (folder or job) specified by the given path.
     */
    void delete(String path);
}

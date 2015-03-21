package net.nemerosa.seed.jenkins;

import java.util.Map;

public interface SeedLauncher {

    void launch(String path, Map<String, String> parameters);

}

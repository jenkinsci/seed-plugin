package net.nemerosa.seed.jenkins;

import net.nemerosa.seed.jenkins.model.SeedEvent;

public interface SeedService {

    void post(SeedEvent event);

}

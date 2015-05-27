package net.nemerosa.seed.generator.scm;

import net.nemerosa.seed.config.SeedProjectEnvironment;

public interface SCMService {

    String getId();

    String generatePartial(SeedProjectEnvironment env, String branch, String path);
}

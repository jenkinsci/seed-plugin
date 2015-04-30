package net.nemerosa.seed.generator.scm;

import net.nemerosa.seed.config.SeedProjectEnvironment;

public interface SCMService {

    public static final String SCM_CREDENTIALS_ID = "scm-credentials-id";

    String getId();

    String generatePartial(SeedProjectEnvironment env, String branch, String path);
}

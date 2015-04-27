package net.nemerosa.seed.jenkins.generator.scm;

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment;

public interface SCMService {

    public static final String SCM_CREDENTIALS_ID = "scm-credentials-id";

    String getId();

    String generatePartial(SeedProjectEnvironment env, String branch, String path);
}

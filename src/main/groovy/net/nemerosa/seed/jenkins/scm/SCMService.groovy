package net.nemerosa.seed.jenkins.scm

import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration

interface SCMService {

    static final String SCM_CREDENTIALS_ID = 'scm-credentials-id'

    void downloadPartial(def scm, SeedProjectConfiguration configuration, String scmUrl, String scmBranch, String path)

}
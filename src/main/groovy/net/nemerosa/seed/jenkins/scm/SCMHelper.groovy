package net.nemerosa.seed.jenkins.scm

import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration

class SCMHelper {

    static void downloadPartial(def scm, SeedProjectConfiguration configuration, String scmType, String scmUrl, String scmBranch, String path) {
        // Gets the SCM
        SCMService service = SCMRegister.get(scmType)
        // Applies the SCM
        service.downloadPartial(scm, configuration, scmUrl, scmBranch, path)
    }

}

package net.nemerosa.seed.jenkins.scm.svn

import net.nemerosa.seed.jenkins.model.SeedProjectConfiguration
import net.nemerosa.seed.jenkins.scm.SCMService

class SVNService implements SCMService {


    @Override
    void downloadPartial(
            def scm, SeedProjectConfiguration configuration, String scmUrl, String scmBranch, String path) {
        scm.svn {
            location("${scmUrl}/${scmBranch}/${path}") {
                directory(path)
                String credentialsId = configuration.getString(SCM_CREDENTIALS_ID, false, '')
                if (credentialsId) {
                    credentials credentialsId
                }
            }
        }
    }

}

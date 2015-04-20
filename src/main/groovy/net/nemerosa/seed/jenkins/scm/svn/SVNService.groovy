package net.nemerosa.seed.jenkins.scm.svn

import net.nemerosa.seed.jenkins.scm.SCMService

class SVNService implements SCMService {

    @Override
    void downloadPartial(def scm, String scmUrl, String scmBranch, String path) {
        scm.svn("${scmUrl}/${scmBranch}/${path}", "${path}")
        // TODO Credentials
    }

}

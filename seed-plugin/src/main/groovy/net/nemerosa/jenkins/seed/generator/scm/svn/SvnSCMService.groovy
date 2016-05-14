package net.nemerosa.jenkins.seed.generator.scm.svn

import net.nemerosa.jenkins.seed.generator.scm.SCMService

class SvnSCMService implements SCMService {

    @Override
    String getId() {
        'svn'
    }

    @Override
    String generatePartial(String scmUrl, String scmCredentials, String branch, String path) {
        """\
scm {
    svn {
        location('${scmUrl}/${branch}/${path}') {
            directory '${path}'
            credentials '${scmCredentials}'
        }
    }
}
"""
    }
}

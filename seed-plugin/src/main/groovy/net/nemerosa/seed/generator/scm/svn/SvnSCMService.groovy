package net.nemerosa.seed.generator.scm.svn

import net.nemerosa.seed.generator.scm.SCMService
import net.nemerosa.seed.config.SeedProjectEnvironment

class SvnSCMService implements SCMService {

    @Override
    String getId() {
        'svn'
    }

    @Override
    String generatePartial(SeedProjectEnvironment env, String branch, String path) {
        """\
scm {
    svn {
        location('${env.scmUrl}/${branch}/${path}') {
            directory '${path}'
            credentials '${env.scmCredentials}'
        }
    }
}
"""
    }
}

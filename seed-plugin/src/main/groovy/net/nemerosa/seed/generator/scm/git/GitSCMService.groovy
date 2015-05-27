package net.nemerosa.seed.generator.scm.git

import net.nemerosa.seed.generator.scm.SCMService
import net.nemerosa.seed.config.SeedProjectEnvironment

class GitSCMService implements SCMService {

    @Override
    String getId() {
        'git'
    }

    @Override
    String generatePartial(SeedProjectEnvironment env, String branch, String path) {
        """\
scm {
    git {
        remote {
            url '${env.scmUrl}'
            credentials '${env.scmCredentials}'
        }
        branch '${branch}'
        configure { node ->
            node / 'extensions' / 'hudson.plugins.git.extensions.impl.CloneOption' {
                'shallow' true
                'reference'()
            }
        }
    }
}
"""
    }
}

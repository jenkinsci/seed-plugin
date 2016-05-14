package net.nemerosa.jenkins.seed.generator.scm.git

import net.nemerosa.jenkins.seed.generator.scm.SCMService

class GitSCMService implements SCMService {

    @Override
    String getId() {
        'git'
    }

    @Override
    String generatePartial(String scmUrl, String scmCredentials, String branch, String path) {
        """\
scm {
    git {
        remote {
            url '${scmUrl}'
            credentials '${scmCredentials}'
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

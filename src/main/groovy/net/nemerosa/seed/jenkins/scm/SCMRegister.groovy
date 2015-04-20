package net.nemerosa.seed.jenkins.scm

import net.nemerosa.seed.jenkins.scm.svn.SVNService

final class SCMRegister {

    private SCMRegister() {
    }

    // TODO Use injection
    private static final Map<String, SCMService> register = [
            svn: new SVNService()
    ]

    static SCMService get(String type) {
        SCMService service = register[type]
        if (service) {
            service
        } else {
            throw new SCMServiceNotDefinedException(type)
        }
    }
}

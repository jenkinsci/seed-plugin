package net.nemerosa.seed.generator.scm

import net.nemerosa.seed.generator.scm.git.GitSCMService
import net.nemerosa.seed.generator.scm.svn.SvnSCMService

class SCMServiceRegistryImpl implements SCMServiceRegistry {

    private final Map<String, SCMService> scmServices = [
            git: new GitSCMService(),
            svn: new SvnSCMService(),
    ]

    @Override
    SCMService getScm(String id) {
        SCMService service = scmServices[id]
        if (service) {
            return service
        } else {
            throw new SCMServiceNotDefinedException(id)
        }
    }
}

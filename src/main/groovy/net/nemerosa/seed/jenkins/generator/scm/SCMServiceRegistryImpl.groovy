package net.nemerosa.seed.jenkins.generator.scm

import net.nemerosa.seed.jenkins.generator.scm.SCMService
import net.nemerosa.seed.jenkins.generator.scm.SCMServiceNotDefinedException
import net.nemerosa.seed.jenkins.generator.scm.SCMServiceRegistry
import net.nemerosa.seed.jenkins.generator.scm.git.GitSCMService

class SCMServiceRegistryImpl implements SCMServiceRegistry {

    private final Map<String, SCMService> scmServices = [
            git: new GitSCMService()
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

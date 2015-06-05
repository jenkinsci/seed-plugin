package net.nemerosa.seed.generator

import com.google.inject.Guice
import com.google.inject.Injector
import net.nemerosa.seed.config.SeedProjectEnvironment
import net.nemerosa.seed.generator.scm.SCMService
import net.nemerosa.seed.generator.scm.SCMServiceModule
import net.nemerosa.seed.generator.scm.SCMServiceRegistry

class BranchSeedScmExtension {

    private final SeedProjectEnvironment projectEnvironment
    private final String scmBranch

    private final Injector injector = Guice.createInjector(new SCMServiceModule())

    BranchSeedScmExtension(SeedProjectEnvironment projectEnvironment, String scmBranch) {
        this.scmBranch = scmBranch
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        // Gets the SCM registry
        SCMServiceRegistry scmServiceRegistry = injector.getInstance(SCMServiceRegistry)
        // Gets the SCM service
        SCMService scmService = scmServiceRegistry.getScm(projectEnvironment.scmType)
        // Generation
        return scmService.generatePartial(projectEnvironment, scmBranch, 'seed')
    }

}

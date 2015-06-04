package net.nemerosa.seed.generator

import net.nemerosa.seed.config.SeedProjectEnvironment

class BranchSeedBranchParametersExtension {

    private final SeedProjectEnvironment projectEnvironment

    BranchSeedBranchParametersExtension(SeedProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        Map<String, String> parameters = projectEnvironment.getParameters('branch-parameters')
        if (!parameters.empty) {
            return parameters.collect { name, description -> "env('${name}', ${name})" }.join('\n')
        } else {
            return ''
        }
    }
}

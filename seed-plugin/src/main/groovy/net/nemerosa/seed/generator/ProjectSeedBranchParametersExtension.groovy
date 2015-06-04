package net.nemerosa.seed.generator

import net.nemerosa.seed.config.SeedProjectEnvironment

class ProjectSeedBranchParametersExtension {

    private final SeedProjectEnvironment projectEnvironment

    ProjectSeedBranchParametersExtension(SeedProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        Map<String, String> parameters = projectEnvironment.getParameters('branch-parameters')
        if (!parameters.empty) {
            return """\
parameters {
    ${
                parameters.collect { name, description ->
                    "stringParam('${name}', '', '${description}')"
                }.join('\n')
            }
}
"""
        } else {
            return ''
        }
    }
}

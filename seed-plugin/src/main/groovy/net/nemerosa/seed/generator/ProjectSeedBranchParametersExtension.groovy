package net.nemerosa.seed.generator

import net.nemerosa.seed.config.SeedProjectEnvironment

class ProjectSeedBranchParametersExtension {

    private final SeedProjectEnvironment projectEnvironment

    ProjectSeedBranchParametersExtension(SeedProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        // Additional parameters
        Map<String, String> parameters = projectEnvironment.getParameters('branch-parameters')
        // Branch SCM?
        if (projectEnvironment.getConfigurationBoolean('branch-scm', false)) {
            parameters.put('BRANCH_SCM', 'Path to the SCM branch')
        }
        // Generating parameter requests
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

package net.nemerosa.seed.generator

import net.nemerosa.seed.config.SeedProjectEnvironment

class ProjectFolderAuthorisationsExtension {

    private final SeedProjectEnvironment projectEnvironment

    ProjectFolderAuthorisationsExtension(SeedProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        def authorisations = projectEnvironment.projectConfiguration.getListString('authorisations')
        if (authorisations && !authorisations.empty) {
            return """\
authorization {
    ${
                authorisations
                        .collect { "permission('${it.replace('*', projectEnvironment.projectConfiguration.name)}')" }
                        .join('\n')
            }
}
"""
        } else {
            return ''
        }
    }
}

package net.nemerosa.seed.jenkins.step

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class ProjectFolderAuthorisations {

    private final SeedProjectEnvironment projectEnvironment

    ProjectFolderAuthorisations(SeedProjectEnvironment projectEnvironment) {
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

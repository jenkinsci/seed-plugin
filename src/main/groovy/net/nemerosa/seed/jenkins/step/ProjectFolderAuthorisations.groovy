package net.nemerosa.seed.jenkins.step

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class ProjectFolderAuthorisations {

    private final SeedProjectEnvironment projectEnvironment

    ProjectFolderAuthorisations(SeedProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        """\
authorization {
    permission('hudson.model.Item.Workspace', 'jenkins_${projectEnvironment.id}')
}
"""
    }
}

package net.nemerosa.jenkins.seed.support

import net.nemerosa.jenkins.seed.config.PipelineConfig

class SeedDSLGenerator {

    static final String seedDsl(String jobName, PipelineConfig config) {
        return """\
job('${jobName}') {
    label 'master'
    parameters {
        stringParam('PROJECT', '', 'Name of the project to generate - used as an identifier')
        choiceParam('PROJECT_SCM_TYPE', ['svn', 'git'])
        stringParam('PROJECT_SCM_URL', '', 'URL to the project SCM location, without any branch location')
        stringParam('PROJECT_SCM_CREDENTIALS', '', 'UUID of the SCM credentials')
        choiceParam('PROJECT_TRIGGER_TYPE', ['', 'github', 'bitbucket', 'http'], 'Trigger type being enabled')
        stringParam('PROJECT_TRIGGER_SECRET', '', 'Trigger secret')
    }
    steps {
        buildDescription('', '\${PROJECT}')
    }
    configure { node ->
        node / 'builders' / 'net.nemerosa.jenkins.seed.generator.ProjectGenerationStep' {
            projectConfig {
                pipelineConfig {
                    destructor ${config.destructor}
                    authorisations '''${config.authorisations ?: ''}'''
                    branchSCMParameter ${config.branchSCMParameter ?: ''}
                    branchParameters '''${config.branchParameters ?: ''}'''
                    generationExtension '''${config.generationExtension ?: ''}'''
                    namingStrategy {
                        projectFolderPath '${config.namingStrategy.projectFolderPath ?: ''}'
                        branchFolderPath '${config.namingStrategy.branchFolderPath ?: ''}'
                        projectSeedName '${config.namingStrategy.projectSeedName ?: ''}'
                        projectDestructorName '${config.namingStrategy.projectDestructorName ?: ''}'
                        branchSeedName '${config.namingStrategy.branchSeedName ?: ''}'
                        branchStartName '${config.namingStrategy.branchStartName ?: ''}'
                        branchName '${config.namingStrategy.branchName ?: ''}'
                        ignoredBranchPrefixes '${config.namingStrategy.ignoredBranchPrefixes ?: ''}'
                    }
                    eventStrategy {
                        delete ${config.eventStrategy.delete}
                        auto ${config.eventStrategy.auto}
                        trigger ${config.eventStrategy.trigger}
                        commit '${config.eventStrategy.commit ?: ''}'
                    }
                }
                project '\${PROJECT}'
                scmType '\${PROJECT_SCM_TYPE}'
                scmUrl '\${PROJECT_SCM_URL}'
                scmCredentials '\${PROJECT_SCM_CREDENTIALS}'
                triggerType '\${PROJECT_TRIGGER_TYPE}'
                triggerSecret '\${PROJECT_TRIGGER_SECRET}'
            }
        }
    }
}
"""
    }

}

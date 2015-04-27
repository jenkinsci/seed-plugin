package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class SeedPipelineGenerator extends AbstractPipelineGenerator {

    @Override
    String generate(SeedProjectEnvironment environment, String branch) {
        String additionalClasspath = environment.getConfigurationValue('pipeline-classpath', '')
        return """\
steps {
    dsl {
        removeAction 'DELETE'
        external 'seed/seed.groovy'
        ignoreExisting false
        additionalClasspath '${additionalClasspath}'
    }
}
"""
    }

}

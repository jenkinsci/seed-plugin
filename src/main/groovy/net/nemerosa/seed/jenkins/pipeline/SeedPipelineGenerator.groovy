package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

// FIXME Reuse the properties based generator
/**
 * Most of the seed steps can reuse the properties based generator (download of dependencies, etc.). Only the last
 * step, with a fixed script, is optional.
 */
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

package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

/**
 * Pipeline generator based on properties found in the <code>seed/seed.properties</code> file.
 *
 * The path to the property file can be configured using the <code>pipeline-generator-property-path</code>
 * configuration property.
 */
class PropertiesPipelineGenerator extends AbstractPipelineGenerator {

    static final String PIPELINE_GENERATOR_PROPERTY_PATH = 'pipeline-generator-property-path'

    @Override
    String generate(SeedProjectEnvironment environment, String branch) {

        List<String> snippets = []

        // Gets the property file name
        String propertyPath = environment.getConfigurationValue(PIPELINE_GENERATOR_PROPERTY_PATH, 'seed/seed.properties')

        // TODO Extensions (injection of DSL steps)

        // PropertyPipelineGeneratorBuilder
        snippets << """\
configure { node ->
    node / 'builders' / 'net.nemerosa.seed.jenkins.pipeline.PropertiesPipelineGeneratorBuilder' {
        'project' '${environment.id}'
        'projectClass' '${environment.projectClass}'
        'projectScmType' '${environment.scmType}'
        'projectScmUrl' '${environment.scmUrl}'
        'branch' '${branch}'
        'propertyPath' '${propertyPath}'
    }
}
"""

        /**
         * Defines a Gradle step which:
         * - downloads the dependencies specified by the previous build (list available as ??? environment variable)
         * - extract the DSL bootstrap script from the indicated JAR (??? and ??? environment variables)
         */
        snippets << """\
steps {
    gradle {
        rootBuildScriptDir 'seed/gradle'
        fromRootBuildScriptDir true
        makeExecutable()
        useWrapper()
        tasks 'prepare'
    }
}
"""


        // FIXME Runs the DSL
        /**
         * Runs the DSL:
         * - defined by the script indicated by the ??? environment variable
         * - with the additional classpath defined by the ??? environment variable
         */

        // OK
        return snippets.join('\n')
    }

}

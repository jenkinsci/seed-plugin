package net.nemerosa.seed.generator

import net.nemerosa.seed.config.Configuration
import net.nemerosa.seed.config.SeedProjectEnvironment

class ProjectSeedExtension {

    static final String PROJECT_SEED_EXTENSIONS = 'project-seed-extensions'

    private final SeedProjectEnvironment environment

    ProjectSeedExtension(SeedProjectEnvironment environment) {
        this.environment = environment
    }

    String generate() {

        List<String> snippets = []

        // Additional parameters
        Map<String, String> parameters = environment.getParameters('branch-parameters')
        // Branch SCM?
        if (environment.getConfigurationBoolean('branch-scm', false)) {
            parameters.put('BRANCH_SCM', 'Path to the SCM branch')
        }
        // Generating parameter requests
        if (!parameters.empty) {
            snippets << """\
parameters {
    ${
                parameters.collect { name, description ->
                    "stringParam('${name}', '', '${description}')"
                }.join('\n')
            }
}
"""
        }

        /**
         * Extensions (injection of DSL steps)
         *
         * Gets the list of extension IDs from the project configuration.
         *
         * Gets the extension snippets from the configuration and applies them.
         */
        def extensionIds = environment.getConfigurationList(PROJECT_SEED_EXTENSIONS)
        extensionIds.each { String extensionId ->
            String extensionDsl = getExtension(extensionId)
            // Adds the extension DSL
            snippets << extensionDsl
        }

        // OK
        return snippets.join('\n')
    }

    protected String getExtension(String id) {
        return Configuration.getFieldInList(
                'extensions',
                environment.projectConfiguration,
                environment.globalConfiguration,
                'id', id,
                'dsl')
    }

}

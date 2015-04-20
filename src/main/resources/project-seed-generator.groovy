import net.nemerosa.seed.jenkins.strategy.naming.SeedNamingStrategyHelper
import net.nemerosa.seed.jenkins.support.SeedDSLHelper

/**
 * Script to generate a project seed.
 *
 * Parameters are:
 *
 * - PROJECT - Name (identifier) of the project
 * - PROJECT_CLASS - Class of project
 * - PROJECT_SCM_TYPE
 * - PROJECT_SCM_URL
 */

def projectHelper = SeedDSLHelper.getProjectHelper(PROJECT as String, PROJECT_CLASS as String)

folder(SeedNamingStrategyHelper.getProjectSeedFolder(projectHelper.namingStrategy, PROJECT as String)) {
    // TODO Authorisations for the project, part of the project configuration
}

freeStyleJob(projectHelper.namingStrategy.getProjectSeed(PROJECT as String)) {
    description "Project seed for ${PROJECT} - generates one branch folder and seed."
    parameters {
        stringParam('BRANCH', '', 'Path to the branch')
    }
    wrappers {
        environmentVariables {
            env('PROJECT', PROJECT)
            env('PROJECT_CLASS', PROJECT_CLASS)
            env('PROJECT_SCM_TYPE', PROJECT_SCM_TYPE)
            env('PROJECT_SCM_URL', PROJECT_SCM_URL)
        }
    }
    scm {
        // TODO Download of the branch code? Might not be needed at this stage
    }
    steps {
        // TODO Generates the branch folder and seed job
        dsl {
            removeAction 'IGNORE' // Existing branches are kept of course
            text SeedDSLHelper.getResourceAsText('/branch-seed-generator.groovy')
            ignoreExisting false  // Always update
        }
    }
}

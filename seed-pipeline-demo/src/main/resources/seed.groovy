import net.nemerosa.seed.demo.GeneralHelper

import static net.nemerosa.seed.demo.GeneralHelper.generalConfiguration

/**
 * Script being called to generate a pipeline
 *
 * The Seed plug-in will give the following parameters to this scripts, available directly as variables:
 *
 * - raw parameters (seed generator input + scm branch)
 *   - PROJECT - raw project name, like nemerosa/seed in GitHub
 *   - PROJECT_CLASS
 *   - PROJECT_SCM_TYPE
 *   - PROJECT_SCM_URL
 *   - BRANCH - basic branch name in the SCM, like branches/xxx in SVN
 *
 * - computed parameters:
 *   - SEED_PROJECT: project normalised name
 *   - SEED_BRANCH: branch normalised name
 *
 * The jobs are generated directly at the level of the branch seed job, so no folder needs to be created for the
 * branch itself.
 */

freeStyleJob("${SEED_PROJECT}_${SEED_BRANCH}_010_BUILD") {
    generalConfiguration delegate, "Build and packaging"
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}_${SEED_BRANCH}_020_CI") {
                condition 'SUCCESS'
                triggerWithNoParameters()
            }
        }
    }
}

freeStyleJob("${SEED_PROJECT}_${SEED_BRANCH}_020_CI") {
    generalConfiguration delegate, "Continous integration"
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}_${SEED_BRANCH}_030_PUBLISH") {
                condition 'SUCCESS'
                triggerWithNoParameters()
            }
        }
    }
}

freeStyleJob("${SEED_PROJECT}_${SEED_BRANCH}_030_PUBLISH") {
    generalConfiguration delegate, "Publication"
}

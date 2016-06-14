package acceptance
/**
 * Sample pipeline generation, used for acceptance tests.
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

job("${SEED_PROJECT}_${SEED_BRANCH}_010_BUILD") {
    parameters {
        stringParam('COMMIT', 'HEAD', 'Commit to build')
    }
    steps {
        shell '''\
echo "Commit: ${COMMIT}"
'''
    }
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}_${SEED_BRANCH}_020_CI") {
                condition 'SUCCESS'
                triggerWithNoParameters()
            }
        }
    }
}

job("${SEED_PROJECT}_${SEED_BRANCH}_020_CI") {
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}_${SEED_BRANCH}_030_PUBLISH") {
                condition 'SUCCESS'
                triggerWithNoParameters()
            }
        }
    }
}

job("${SEED_PROJECT}_${SEED_BRANCH}_030_PUBLISH") {

}

queue("${SEED_PROJECT}_${SEED_BRANCH}_010_BUILD")

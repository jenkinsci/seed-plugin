package acceptance
/**
 * Sample pipeline generation, used for acceptance tests.
 *
 * The Seed plug-in will give the following parameters to this scripts, available directly as variables:
 *
 * - raw parameters (seed generator input + scm branch)
 *   - PROJECT - raw project name, like nemerosa/seed in GitHub
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

job("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    parameters {
        stringParam('COMMIT', 'HEAD', 'Commit to build')
    }
    steps {
        shell '''\
echo "Project: ${PROJECT}"
echo "Branch: ${BRANCH}"
echo "Seed project: ${SEED_PROJECT}"
echo "Seed branch: ${SEED_BRANCH}"
echo "SCM type: ${PROJECT_SCM_TYPE}"
echo "SCM URL: ${PROJECT_SCM_URL}"
'''
    }
}

queue("${SEED_PROJECT}-${SEED_BRANCH}-build")

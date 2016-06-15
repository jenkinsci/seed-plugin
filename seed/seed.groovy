/**
 * Pipeline generation for the Seed plug-in.
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

job("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    logRotator(-1, 40)
    jdk 'JDK7'
    parameters {
        stringParam('COMMIT', 'HEAD', 'Commit to build')
    }
    scm {
        git {
            remote {
                url PROJECT_SCM_URL
                branch "origin/${BRANCH}"
            }
            extensions {
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
    }
    steps {
        gradle 'clean build --info --profile'
    }
    publishers {
        archiveJunit("**/build/test-results/*.xml")
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/build/**,seed/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
    }
}

queue "${SEED_PROJECT}-${SEED_BRANCH}-build"

/**
 * Release job
 */

if (BRANCH.startsWith('release/')) {

    job("${SEED_PROJECT}-${SEED_BRANCH}-publish") {
        logRotator(-1, 40)
        jdk 'JDK7'
        parameters {
            stringParam('COMMIT', 'HEAD', 'Commit to build')
        }
        scm {
            git {
                remote {
                    url PROJECT_SCM_URL
                    branch "origin/${BRANCH}"
                }
                extensions {
                    wipeOutWorkspace()
                    localBranch "${BRANCH}"
                }
            }
        }
        steps {
            gradle 'clean versionFile publish --info --profile'
            environmentVariables {
                propertiesFile 'build/version.properties'
            }
            shell '''\
git tag ${VERSION_DISPLAY}
git push origin ${VERSION_DISPLAY}
'''
        }
    }

}
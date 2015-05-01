package net.nemerosa.seed.acceptance

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicLong

/**
 * Testing the triggering of seeds and pipelines using the Seed plug-in.
 */
@RunWith(AcceptanceTestRunner)
class SeedTriggeringTest {

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    private static final AtomicLong counter = new AtomicLong()

    static String uid(String prefix) {
        prefix + new SimpleDateFormat('mmssSSS').format(new Date()) + counter.incrementAndGet()
    }

    @Before
    void 'Project preparation'() {
        // Checks the seed is there
        jenkins.job('seed')
    }

    @Test
    void 'Default seed tree'() {
        // Project name
        def project = uid('P')
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}")
        // Checks the branch seed is created
        jenkins.job("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        jenkins.post("seed-http/seed?project=${project}&branch=master")
        // Checks the branch pipeline is there
        jenkins.job("${project}/${project}-master/${project}-master-build")
        jenkins.job("${project}/${project}-master/${project}-master-ci")
        jenkins.job("${project}/${project}-master/${project}-master-publish")
        // Fires the branch pipeline start
        jenkins.post("seed-http/commit?project=${project}&branch=master")
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-publish", 1).checkSuccess()
    }

    // TODO Test for not enabled global configuration
    // TODO Test for not enabled project
    // TODO Test for enabled project in not enabled configuration

}

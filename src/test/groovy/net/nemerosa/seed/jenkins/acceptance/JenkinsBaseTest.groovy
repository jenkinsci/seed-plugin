package net.nemerosa.seed.jenkins.acceptance

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Is the Jenkins application available?
 */
@RunWith(AcceptanceTestRunner)
class JenkinsBaseTest {

    public static final int JOB_TIMEOUT = 1 * 60

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Test
    void 'Jenkins started'() {
    }

    @Test
    void 'Default seed job created'() {
        // TODO Checks the job
        jenkins.job('seed', JOB_TIMEOUT, JOB_TIMEOUT)
    }

    @Test
    void 'Creating a complete seed tree'() {
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : 'test',
                PROJECT_SCM_TYPE: 'GIT',
                // TODO Path configuration
                PROJECT_SCM_URL : 'path/to/repo',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job('test/test-seed')
        // TODO Fires the project seed
        // TODO Fires the branch seed
        // TODO Fires the branch pipeline start
        // TODO Checks the result of the pipeline
    }

}

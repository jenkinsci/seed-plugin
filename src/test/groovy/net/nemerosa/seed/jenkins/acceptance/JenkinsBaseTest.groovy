package net.nemerosa.seed.jenkins.acceptance

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Is the Jenkins application available?
 */
@RunWith(AcceptanceTestRunner)
class JenkinsBaseTest {

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Test
    void 'Jenkins started'() {
    }

    @Test
    void 'Default seed job created'() {
        // TODO Checks the job
        jenkins.job('seed', 5 * 60, 5 * 60)
    }

}

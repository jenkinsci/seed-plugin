package net.nemerosa.seed.jenkins.docker

import org.junit.Rule
import org.junit.Test

/**
 * Is the Jenkins application available?
 */
class JenkinsBaseTest {

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Test
    void 'Jenkins started'() {

    }

}

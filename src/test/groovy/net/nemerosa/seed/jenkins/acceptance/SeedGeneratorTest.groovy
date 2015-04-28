package net.nemerosa.seed.jenkins.acceptance

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing the generation of seeds and pipelines using the Seed plug-in.
 */
@RunWith(AcceptanceTestRunner)
class SeedGeneratorTest {

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
        // Checks the seed job exists
        'Default seed job created'()
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : 'test',
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job('test/test-seed')
        // Fires the project seed
        jenkins.fireJob('test/test-seed', [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job('test/test-master/test-master-seed')
        // Fires the branch seed
        jenkins.fireJob('test/test-master/test-master-seed').checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job('test/test-master/test-master-build')
        jenkins.job('test/test-master/test-master-ci')
        jenkins.job('test/test-master/test-master-publish')
        // Fires the branch pipeline start
        jenkins.fireJob('test/test-master/test-master-build').checkSuccess()
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild('test/test-master/test-master-ci', 1).checkSuccess()
        jenkins.getBuild('test/test-master/test-master-publish', 1).checkSuccess()
    }

    @Test
    void 'Project folder authorisations'() {
        // Checks the seed job exists
        'Default seed job created'()
        // Configuration of the Seed job
        jenkins.configureSeed '''\
classes:
    - id: custom-auth
      authorisations:
          - hudson.model.Item.Workspace:jenkins_*
          - hudson.model.Item.Read:jenkins_*
          - hudson.model.Item.Discover:jenkins_*
'''
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : 'test-auth',
                PROJECT_CLASS   : 'custom-auth',
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : 'path/to/repo',
        ]).checkSuccess()
        // Checks the project folder is created
        jenkins.job('test-auth')
        // Checks the project folder authorisation matrix
        def xml = jenkins.jobConfig('test-auth')
        def matrix = xml.properties['com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty']
        assert matrix
        assert matrix.permission.collect { it.text() as String } == [
                'hudson.model.Item.Workspace:jenkins_test-auth',
                'hudson.model.Item.Read:jenkins_test-auth',
                'hudson.model.Item.Discover:jenkins_test-auth',
        ]
    }

}

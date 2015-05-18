package net.nemerosa.seed.acceptance

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
        jenkins.fireJob('test/test-master/test-master-build', [COMMIT: 'HEAD']).checkSuccess()
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

    @Test
    void 'Creating a project tree based of full customisation'() {
        // Checks the seed job exists
        'Default seed job created'()
        // Configuration
        jenkins.configureSeed '''\
strategies:
  - id: custom
    seed-expression: "${PROJECT}/${PROJECT}_GENERATOR"
    branch-seed-expression: "${PROJECT}/${PROJECT}_*/${PROJECT}_*_GENERATOR"
    branch-start-expression: "${PROJECT}/${PROJECT}_*/${PROJECT}_*_010_BUILD"
    branch-name-expression: "${BRANCH}"
    branch-name-prefixes:
      - "branches/"
    commit-parameter: "REVISION"
classes:
    - id: custom-pipeline
      branch-strategy: custom
'''
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : 'PRJ',
                PROJECT_CLASS   : 'custom-pipeline',
                PROJECT_SCM_TYPE: 'svn',
                PROJECT_SCM_URL : 'svn://localhost/PRJ',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job('PRJ/PRJ_GENERATOR')
        // Fires the project seed
        jenkins.fireJob('PRJ/PRJ_GENERATOR', [
                BRANCH: 'branches/R11.7.0'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_GENERATOR')
        // Fires the branch seed (extra timeout because of Gradle runtime download)
        jenkins.fireJob('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_GENERATOR', [:], 600).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_010_BUILD')
        jenkins.job('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_020_CI')
        jenkins.job('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_030_PUBLISH')
        // Fires the branch pipeline start
        jenkins.fireJob('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_010_BUILD').checkSuccess()
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_020_CI', 1).checkSuccess()
        jenkins.getBuild('PRJ/PRJ_R11.7.0/PRJ_R11.7.0_030_PUBLISH', 1).checkSuccess()
    }

    @Test
    void 'Pipeline extensions'() {
        // Checks the seed job exists
        'Default seed job created'()
        // Project name
        String project = TestUtils.uid('P')
        // Configuration
        jenkins.configureSeed """\
pipeline-extensions:
    - id: extension1
      dsl: |
        steps {
            shell "echo Extension 1"
        }
    - id: extension2
      dsl: |
        steps {
            shell "echo Extension 2"
        }
projects:
    - id: "${project}"
      pipeline-generator-extensions:
        - extension1
        - extension2
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${project}/${project}-master/${project}-master-build")
        jenkins.job("${project}/${project}-master/${project}-master-ci")
        jenkins.job("${project}/${project}-master/${project}-master-publish")
        // Gets the branch seed build...
        def branchSeedBuild = jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1)
        // ... gets its output
        def branchSeedBuildOutput = new URL(branchSeedBuild.json.url + 'consoleText').text
        // ... and checks it contains the customisations
        assert branchSeedBuildOutput.contains('Extension 1')
        assert branchSeedBuildOutput.contains('Extension 2')
    }

}

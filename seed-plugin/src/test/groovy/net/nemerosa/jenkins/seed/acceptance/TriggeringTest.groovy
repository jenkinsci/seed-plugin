package net.nemerosa.jenkins.seed.acceptance

import net.nemerosa.jenkins.seed.config.EventStrategyConfig
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.test.AcceptanceTestRunner
import net.nemerosa.jenkins.seed.test.JenkinsAPIRefusedException
import net.nemerosa.jenkins.seed.test.JenkinsAccessRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.TimeoutException

import static net.nemerosa.jenkins.seed.test.TestUtils.uid
import static org.junit.Assert.fail

/**
 * Testing the triggering of seeds and pipelines using the Seed plug-in.
 */
@RunWith(AcceptanceTestRunner)
class TriggeringTest {

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Test
    void 'Default seed tree'() {
        // Project name
        def project = uid('p')
        // Configuration
        def seed = jenkins.defaultSeed()
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
                PROJECT_TRIGGER_TYPE: 'http',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http-api/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        // jenkins.post("seed-http/seed?project=${project}&branch=master")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${project}/${project}-master/${project}-master-build")
        jenkins.job("${project}/${project}-master/${project}-master-ci")
        jenkins.job("${project}/${project}-master/${project}-master-publish")
        // Fires the branch pipeline start
        jenkins.post("seed-http-api/commit?project=${project}&branch=master")
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-publish", 1).checkSuccess()
    }

    @Test(expected = JenkinsAPIRefusedException)
    @Ignore
    void 'HTTP API not being enabled at global configuration level'() {
        // Project name
        def project = uid('P')
        // Configuration
        jenkins.configureSeed '''\
http-enabled: no
'''
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
        jenkins.post("seed-http/create?project=${project}&branch=master")
    }

    @Test(expected = JenkinsAPIRefusedException)
    @Ignore
    void 'HTTP API not being enabled at project configuration level'() {
        // Project name
        def project = uid('P')
        // Configuration
        jenkins.configureSeed """\
projects:
    - id: ${project}
      http-enabled: no
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
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
    }

    @Test
    @Ignore
    void 'HTTP API being enabled at project configuration level and not at global level'() {
        // Project name
        def project = uid('P')
        // Configuration
        jenkins.configureSeed """\
http-enabled: no
projects:
    - id: ${project}
      http-enabled: yes
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
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
    }

    @Test(expected = JenkinsAPIRefusedException)
    void 'Token not provided'() {
        // Project name
        def project = uid('p')
        // Configuration
        def seed = jenkins.defaultSeed()
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
                PROJECT_TRIGGER_TYPE: 'http',
                PROJECT_TRIGGER_SECRET: 'ABCDEF',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http-api/create?project=${project}&branch=master")
    }

    @Test
    void 'Token provided'() {
        // Project name
        def project = uid('p')
        // Configuration
        def seed = jenkins.defaultSeed()
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
                PROJECT_TRIGGER_TYPE: 'http',
                PROJECT_TRIGGER_SECRET: 'ABCDEF',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http-api/create?project=${project}&branch=master", { HttpURLConnection c ->
            c.setRequestProperty('X-Seed-Token', 'ABCDEF')
        })
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
    }

    @Test
    void 'No pipeline generation when auto is set to false'() {
        // Project name
        def project = uid('p')
        // Configuration
        def seed = jenkins.seed(
                new PipelineConfig()
                    .withEventStrategy(new EventStrategyConfig().withAuto(false))
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
                PROJECT_TRIGGER_TYPE: 'http',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http-api/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks that the master seed was NOT fired
        try {
            jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 2, 30).checkSuccess()
            fail "The master branch automatic generation is not enabled"
        } catch (TimeoutException ignored) {
            // OK
        }
    }

    @Test
    @Ignore
    void 'Commit event with a custom naming strategy'() {
        // Project name
        def project = uid('p')
        // Configuration
        jenkins.configureSeed """\
strategies:
  - id: ci
    branch-start-expression: "\${project}/\${project}-*/\${project}-*-ci"
    commit-parameter: SVN_REVISION
classes:
    - id: my-class
      branch-strategy: ci
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_CLASS   : 'my-class',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-ci',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created - it's fired automatically
        jenkins.job("${project}/${project}-master/${project}-master-seed")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there - it's fired automatically
        jenkins.job("${project}/${project}-master/${project}-master-ci")
        // Checks the first build - it's fired automatically on Seed generation
        jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 1).checkSuccess()
        // Triggers again the branch pipeline
        jenkins.post("seed-http/commit?project=${project}&branch=master&commit=100")
        // Checks that the master pipeline is fired
        def build = jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 2)
        build.checkSuccess()
        assert build.output.contains('Commit: 100')
    }

}

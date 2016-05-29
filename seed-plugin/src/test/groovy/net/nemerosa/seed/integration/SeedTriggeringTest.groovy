package net.nemerosa.seed.integration

import hudson.model.ChoiceParameterDefinition
import hudson.model.ParametersDefinitionProperty
import hudson.model.StringParameterDefinition
import net.nemerosa.jenkins.seed.integration.SeedRule
import net.nemerosa.jenkins.seed.integration.git.GitRepo
import net.nemerosa.jenkins.seed.test.JenkinsForbiddenException
import net.nemerosa.seed.generator.ProjectSeedBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import static net.nemerosa.jenkins.seed.test.TestUtils.uid

/**
 * Testing the triggering of seeds and pipelines using the Seed plug-in.
 */
class SeedTriggeringTest {

    @Rule
    public SeedRule jenkins = new SeedRule()

    @Before
    void 'Default seed job created'() {
        // Creation of the `seed` job
        def seed = jenkins.createFreeStyleProject('seed')
        seed.addProperty(new ParametersDefinitionProperty(
                new StringParameterDefinition('PROJECT', ''),
                new StringParameterDefinition('PROJECT_CLASS', ''),
                new ChoiceParameterDefinition('PROJECT_SCM_TYPE', ['git', 'svn'] as String[], ''),
                new StringParameterDefinition('PROJECT_SCM_URL', ''),
                new StringParameterDefinition('PROJECT_SCM_CREDENTIALS', ''),
        ))
        seed.buildersList << new ProjectSeedBuilder(
                '${PROJECT}',
                '${PROJECT_CLASS}',
                '${PROJECT_SCM_TYPE}',
                '${PROJECT_SCM_URL}',
                '${PROJECT_SCM_CREDENTIALS}',
        )
        // Makes sure the configuration is empty
        jenkins.configureSeed ''
    }

    @Test
    void 'Default seed tree'() {
        // Project name
        def project = uid('p')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed ''
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        // jenkins.post("seed-http/seed?project=${project}&branch=master")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-build")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-publish")
        // Fires the branch pipeline start (not needed, but have been fired automatically by the seed)
        // jenkins.post("seed-http/commit?project=${project}&branch=master")
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 1).checkSuccess()
        jenkins.getBuild("${project}/${project}-master/${project}-master-publish", 1).checkSuccess()
    }

    @Test(expected = JenkinsForbiddenException)
    void 'HTTP API not being enabled at global configuration level'() {
        // Project name
        def project = uid('P')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed '''\
http-enabled: no
'''
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
    }

    @Test(expected = JenkinsForbiddenException)
    void 'HTTP API not being enabled at project configuration level'() {
        // Project name
        def project = uid('P')
        // Git
        def git = GitRepo.prepare('std')
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
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
    }

    @Test
    void 'HTTP API being enabled at project configuration level and not at global level'() {
        // Project name
        def project = uid('P')
        // Git
        def git = GitRepo.prepare('std')
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
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
    }

    @Test(expected = JenkinsForbiddenException)
    void 'Token not provided'() {
        // Project name
        def project = uid('p')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed """\
http-secret-key: ABCDEF
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
    }

    @Test
    void 'Token provided'() {
        // Project name
        def project = uid('p')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed """\
http-secret-key: ABCDEF
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master", '', [
                'X-Seed-Token': 'ABCDEF'
        ])
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
    }

    @Test
    void 'No pipeline generation when pipeline-auto is set to false in a class'() {
        // Project name
        def project = uid('P')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed """\
classes:
    - id: my-class
      pipeline-auto: false
projects:
    - id: ${project}
      project-class: my-class
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-build")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-publish")
        // Triggers again the branch generator
        jenkins.post("seed-http/seed?project=${project}&branch=master")
        // Checks that the master seed was NOT fired
        def build = jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 2, 30)
        assert build == null: "The master branch automatic generation is not enabled"
    }

    @Test
    void 'No pipeline started when pipeline-trigger is set to false in a class'() {
        // Project name
        def project = uid('P')
        // Git
        def git = GitRepo.prepare('std')
        // Configuration
        jenkins.configureSeed """\
classes:
    - id: my-class
      pipeline-trigger: no
projects:
    - id: ${project}
      project-class: my-class
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-build")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-publish")
        // Checks the first build - it's fired automatically on Seed generation
        jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1).checkSuccess()
        // Triggers again the branch pipeline
        jenkins.post("seed-http/commit?project=${project}&branch=master")
        // Checks that the master pipeline was NOT fired
        def build = jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 2, 30)
        assert build == null: "The master branch automatic generation is not enabled"
    }

    @Test
    void 'Commit event with a custom naming strategy'() {
        // Project name
        def project = uid('p')
        // Git
        def git = GitRepo.prepare('ci')
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
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed for the `master` branch
        jenkins.post("seed-http/create?project=${project}&branch=master")
        // Checks the result of the project seed
        jenkins.getBuild("${project}/${project}-seed", 1).checkSuccess()
        // Checks the branch seed is created - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Checks the result of the branch seed
        jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1).checkSuccess()
        // Checks the branch pipeline is there - it's fired automatically
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
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

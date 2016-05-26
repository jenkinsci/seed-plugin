package net.nemerosa.jenkins.seed.integration

import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.integration.git.GitRepo
import org.junit.Rule
import org.junit.Test

import static net.nemerosa.jenkins.seed.test.TestUtils.uid

/**
 * Before launching the tests in the IDE, make to run, at least once:
 *
 * ./gradlew resolveTestDependencies
 *
 * This will make sure the plugin dependencies are correctly setup in the test Jenkins working copy.
 *
 * In the regular Gradle build, no need to do anything.
 */
class GenerationIntegrationTest {

    @Rule
    public SeedRule jenkins = new SeedRule();

    @Test
    void 'Project seed'() {
        // Project name
        def projectName = uid('p')
        // Creates a seed job
        def seed = jenkins.defaultSeed()
        // Fires the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : '',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-seed")
    }

    @Test
    void 'Creating a complete seed tree'() {
        // Default seed
        String seed = jenkins.defaultSeed()
        // Project name
        def projectName = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-seed")
        // Fires the branch seed
        def output = jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess().output
        println "OUTPUT ${projectName}-master-seed:\n${output}"
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-build")
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-ci")
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-publish")
        // Fires the branch pipeline start
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-build", [COMMIT: 'HEAD']).checkSuccess()
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${projectName}/${projectName}-master/${projectName}-master-ci", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}-master/${projectName}-master-publish", 1).checkSuccess()
    }

    @Test
    void 'Custom environment variable'() {
        // Project name
        def projectName = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('env')
        // Configuration of environment variables
        String seed = jenkins.seed(
                new PipelineConfig()
                        .withBranchParameters('BRANCH_PARAM: Additional parameter')
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH      : 'master',
                BRANCH_PARAM: 'test',
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-build")
        // Fires the branch pipeline start
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-build", [COMMIT: 'HEAD']).checkSuccess()
    }

    @Test
    void 'Branch SCM parameter'() {
        // Project name
        def projectName = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('scm')
        // Configuration of environment variables
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withBranchSCMParameter(true)
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH    : '1.0',
                BRANCH_SCM: 'master',
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-1.0/${projectName}-1.0-seed")
        // Fires the branch seed
        jenkins.fireJob("${projectName}/${projectName}-1.0/${projectName}-1.0-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}-1.0/${projectName}-1.0-build")
        // Fires the branch pipeline start
        def build = jenkins.fireJob("${projectName}/${projectName}-1.0/${projectName}-1.0-build", [COMMIT: 'HEAD'])
        build.checkSuccess()
        assert build.output.contains('Branch SCM: master')
    }

    @Test
    void 'Project folder authorisations'() {
        // Project name
        def projectName = uid('p')
        // Configuration of the Seed job
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withAuthorisations('''\
                        hudson.model.Item.Workspace:jenkins_*
                        hudson.model.Item.Read:jenkins_*
                        # Comments and empty lines are allowed

                        hudson.model.Item.Discover:jenkins_*
                        ''')
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : 'path/to/repo',
        ]).checkSuccess()
        // Checks the project folder is created
        jenkins.checkJobExists(projectName)
        // Checks the project folder authorisation matrix
        def xml = jenkins.jobConfig(projectName)
        def matrix = xml.properties['com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty']
        assert matrix
        assert matrix.permission.collect { it.text() as String } == [
                "hudson.model.Item.Workspace:jenkins_${projectName}",
                "hudson.model.Item.Read:jenkins_${projectName}",
                "hudson.model.Item.Discover:jenkins_${projectName}",
        ] as List<String>
    }
}

package net.nemerosa.jenkins.seed.integration

import net.nemerosa.jenkins.seed.config.NamingStrategyConfig
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

    @Test
    void 'Project pipeline extensions'() {
        // Project name
        String project = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Configuration
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withGenerationExtension('''\
                        steps {
                            shell "echo Extension 1"
                            shell "echo Extension 2"
                        }
                        ''')
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Gets the project seed build...
        def projectSeedBuild = jenkins.getBuild("${project}/${project}-seed", 1)
        // ... gets its output
        def projectSeedBuildOutput = projectSeedBuild.output
        // ... and checks it contains the customisations
        assert projectSeedBuildOutput.contains('Extension 1')
        assert projectSeedBuildOutput.contains('Extension 2')
    }

    @Test
    void 'Pipeline is not fired after generation'() {
        // Project name
        String project = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('cinoqueue')
        // Default configuration
        def seed = jenkins.defaultSeed()
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
        // Checks the result of the pipeline (ci must NOT have been fired automatically)
        def build = jenkins.getBuild("${project}/${project}-master/${project}-master-ci", 1, 30)
        assert build == null: "The pipeline should not have been fired"
    }

    @Test
    void 'Destructor job'() {
        // Project name
        String project = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Default configuration
        def seed = jenkins.seed(
                new PipelineConfig().withDestructor(true)
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()

        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}-seed")
        // Checks the destructor job is created
        jenkins.checkJobExists("${project}/${project}-destructor")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")

        // Fires the destructor
        jenkins.fireJob("${project}/${project}-destructor", [
                BRANCH: 'master'
        ]).checkSuccess()

        // Checks the branch folder is gone
        jenkins.gone("${project}/${project}-master")
    }

    @Test
    void 'Destructor job with custom naming convention'() {
        // Project name
        String project = uid('P')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Default configuration
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withDestructor(true)
                        .withNamingStrategy(
                        new NamingStrategyConfig()
                                .withProjectFolderPath('${PROJECT}')
                                .withProjectSeedName('${PROJECT}_GENERATOR')
                                .withProjectDestructorName('${PROJECT}_DESTRUCTOR')
                                .withBranchFolderPath('${PROJECT}_*')
                                .withBranchSeedName('${PROJECT}_*_GENERATOR')
                                .withBranchStartName('${PROJECT}_*_010_BUILD')
                                .withBranchName('${BRANCH}')
                                .withIgnoredBranchPrefixes('branches/')
                )
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()

        // Checks the project seed is created
        jenkins.checkJobExists("${project}/${project}_GENERATOR")
        // Checks the destructor job is created
        jenkins.checkJobExists("${project}/${project}_DESTRUCTOR")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}_GENERATOR", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}_MASTER/${project}_MASTER_GENERATOR")

        // Fires the destructor
        jenkins.fireJob("${project}/${project}_DESTRUCTOR", [
                BRANCH: 'master'
        ]).checkSuccess()

        // Checks the branch folder is gone
        jenkins.gone("${project}/${project}_MASTER")
    }
}
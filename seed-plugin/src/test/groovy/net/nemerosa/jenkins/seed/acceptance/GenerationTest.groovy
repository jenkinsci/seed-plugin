package net.nemerosa.jenkins.seed.acceptance

import net.nemerosa.jenkins.seed.config.NamingStrategyConfig
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.test.AcceptanceTestRunner
import net.nemerosa.jenkins.seed.test.JenkinsAccessRule
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.TimeoutException

import static net.nemerosa.jenkins.seed.test.TestUtils.uid
import static org.junit.Assert.fail

/**
 * Testing the generation of seeds and pipelines using the Seed plug-in.
 */
@RunWith(AcceptanceTestRunner)
class GenerationTest {

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Before
    void 'Seed generator created'() {
        jenkins.job('seed-generator')
    }

    @Test
    void 'Default seed job created'() {
        jenkins.defaultSeed()
    }

    @Test
    void 'Project seed'() {
        // Default seed
        String seed = jenkins.defaultSeed()
        // Project name
        def projectName = uid('p')
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${projectName}/${projectName}-seed")
    }

    @Test
    void 'Creating a complete seed tree'() {
        // Default seed
        String seed = jenkins.defaultSeed()
        // Project name
        def projectName = uid('p')
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-build")
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-ci")
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-publish")
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
        // Configuration of environment variables
        String seed = jenkins.seed(
                new PipelineConfig()
                        .withBranchParameters('BRANCH_PARAM: Additional parameter')
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-env',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH      : 'master',
                BRANCH_PARAM: 'test',
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${projectName}/${projectName}-master/${projectName}-master-build")
        // Fires the branch pipeline start
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-build", [COMMIT: 'HEAD']).checkSuccess()
    }

    @Test
    void 'Branch SCM parameter'() {
        // Project name
        def projectName = uid('p')
        // Configuration of environment variables
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withBranchSCMParameter(true)
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-scm',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${projectName}/${projectName}-seed")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}-seed", [
                BRANCH    : '1.0',
                BRANCH_SCM: 'master',
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${projectName}/${projectName}-1.0/${projectName}-1.0-seed")
        // Fires the branch seed
        jenkins.fireJob("${projectName}/${projectName}-1.0/${projectName}-1.0-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${projectName}/${projectName}-1.0/${projectName}-1.0-build")
        // Fires the branch pipeline start
        def build = jenkins.fireJob("${projectName}/${projectName}-1.0/${projectName}-1.0-build", [COMMIT: 'HEAD'])
        build.checkSuccess()
        assert build.output.contains('Branch SCM: master')
    }

    // TODO Direct script execution?
    @Test
    @Ignore
    void 'Direct script execution - not allowed'() {
        // Project name
        String project = uid('p')
        // Configuration of the Seed job
        jenkins.configureSeed '''\
classes:
    - id: my-class
      pipeline-generator-script-allowed: no
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
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkFailure()
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
        jenkins.job(projectName)
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
    void 'Creating a project tree based of full customisation'() {
        // Configuration
        // @formatter:off
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withNamingStrategy(
                            new NamingStrategyConfig()
                                .withProjectFolderPath('${PROJECT}')
                                .withProjectSeedName('${PROJECT}_GENERATOR')
                                .withBranchFolderPath('${PROJECT}_*')
                                .withBranchSeedName('${PROJECT}_*_GENERATOR')
                                .withBranchStartName('${PROJECT}_*_010_BUILD')
                                .withBranchName('${BRANCH}')
                                .withIgnoredBranchPrefixes('branches/')
                                // TODO Commit parameter? REVISION
                        )
        )
        // @formatter:on
        // Project name
        def projectName = uid('P')
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'svn',
                PROJECT_SCM_URL : 'svn://localhost/PRJ',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${projectName}/${projectName}_GENERATOR")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}_GENERATOR", [
                BRANCH: 'branches/R11.7.0'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_GENERATOR")
        // Fires the branch seed (extra timeout because of Gradle runtime download)
        jenkins.fireJob("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_GENERATOR", [:], 600).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.job("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_010_BUILD")
        jenkins.job("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_020_CI")
        jenkins.job("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_030_PUBLISH")
        // Fires the branch pipeline start
        jenkins.fireJob("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_010_BUILD").checkSuccess()
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_020_CI", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}_R11.7.0/${projectName}_R11.7.0_030_PUBLISH", 1).checkSuccess()
    }

    @Test
    @Ignore
    void 'Branch pipeline extensions'() {
        // Project name
        String project = uid('p')
        // Configuration
        jenkins.configureSeed """\
extensions:
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
        def branchSeedBuildOutput = branchSeedBuild.output
        // ... and checks it contains the customisations
        assert branchSeedBuildOutput.contains('Extension 1')
        assert branchSeedBuildOutput.contains('Extension 2')
    }

    @Test
    void 'Project pipeline extensions'() {
        // Project name
        String project = uid('p')
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
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
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
        // Default configuration
        def seed = jenkins.defaultSeed()
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-cinoqueue',
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
        // Checks the result of the pipeline (build must NOT have been fired automatically)
        try {
            jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1, 30).checkSuccess()
            fail "The pipeline should not have been fired"
        } catch (TimeoutException ignored) {
            // OK
        }
    }

    @Test
    void 'Destructor job'() {
        // Project name
        String project = uid('p')
        // Default configuration
        def seed = jenkins.seed(
                new PipelineConfig().withDestructor(true)
        )
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()

        // Checks the project seed is created
        jenkins.job("${project}/${project}-seed")
        // Checks the destructor job is created
        jenkins.job("${project}/${project}-destructor")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}-seed", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${project}/${project}-master/${project}-master-seed")

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
                // Path to the prepared Git repository in docker.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()

        // Checks the project seed is created
        jenkins.job("${project}/${project}_GENERATOR")
        // Checks the destructor job is created
        jenkins.job("${project}/${project}_DESTRUCTOR")
        // Fires the project seed
        jenkins.fireJob("${project}/${project}_GENERATOR", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.job("${project}/${project}_MASTER/${project}_MASTER_GENERATOR")

        // Fires the destructor
        jenkins.fireJob("${project}/${project}_DESTRUCTOR", [
                BRANCH: 'master'
        ]).checkSuccess()

        // Checks the branch folder is gone
        jenkins.gone("${project}/${project}_MASTER")
    }

}

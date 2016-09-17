package net.nemerosa.jenkins.seed.integration

import net.nemerosa.jenkins.seed.config.EventStrategyConfig
import net.nemerosa.jenkins.seed.config.NamingStrategyConfig
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.integration.git.GitRepo
import net.nemerosa.jenkins.seed.integration.svn.SVNRepo
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

    @Test
    void 'Branch pipeline extensions'() {
        // Project name
        String project = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Configuration
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withPipelineGenerationExtension('''\
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
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-build")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-ci")
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-publish")
        // Gets the branch seed build...
        def branchSeedBuild = jenkins.getBuild("${project}/${project}-master/${project}-master-seed", 1)
        // ... gets its output
        def branchSeedBuildOutput = branchSeedBuild.output
        // ... and checks it contains the customisations
        assert branchSeedBuildOutput.contains('Extension 1')
        assert branchSeedBuildOutput.contains('Extension 2')
    }

    @Test
    void 'Direct script execution - not allowed'() {
        // Project name
        String project = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std')
        // Configuration
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withDisableDslScript(true)
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
        // Checks the branch seed is created
        jenkins.checkJobExists("${project}/${project}-master/${project}-master-seed")
        // Fires the branch seed - it must fail
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkFailure()
    }

    @Test
    void 'SVN pipeline'() {
        // Project name
        def projectName = uid('p')
        // Configuration
        // @formatter:off
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withNamingStrategy(
                            new NamingStrategyConfig()
                                .withIgnoredBranchPrefixes('branches/')
                        )
                        // TODO SVN Disable auto generation of the pipeline until possible to test with SVN
                        .withEventStrategy(
                            new EventStrategyConfig()
                                .withAuto(false)
                        )
        )
        // @formatter:on
        // With a SVN repository
        SVNRepo.withPreparedSvnRepo(projectName, 'branches/11.7.0', 'svn') { SVNRepo svn ->
            // Firing the seed job
            jenkins.fireJob(seed, [
                    PROJECT         : projectName,
                    PROJECT_SCM_TYPE: 'svn',
                    PROJECT_SCM_URL : svn.getUrlForPath(projectName),
            ]).checkSuccess()
            // Checks the project seed is created
            jenkins.checkJobExists("${projectName}/${projectName}-seed")
            // TODO SVN Fires the project seed
            // jenkins.fireJob("${projectName}/${projectName}-seed", [
            //         BRANCH: 'branches/11.7.0'
            // ]).checkSuccess()
            // Checks the branch seed is created
            // jenkins.checkJobExists("${projectName}/${projectName}-11.7.0/${projectName}-11.7.0-seed")
            // Checks the SVN configuration is OK
            // TODO SVN Disable auto generation of the pipeline until possible to test with SVN
            // Checks the pipeline seed had been fired with success
            // jenkins.getBuild("${projectName}/${projectName}-11.7.0/${projectName}-11.7.0-seed", 1).checkSuccess()
            // Checks the branch pipeline is there
            // jenkins.checkJobExists("${projectName}/${projectName}-11.7.0/${projectName}-11.7.0-build")
            // Checks the result of the pipeline (build must have been fired automatically by the DSL)
            // jenkins.getBuild("${projectName}/${projectName}-11.7.0/${projectName}-11.7.0-build", 1).checkSuccess()
        }
    }

    @Test
    void 'Custom naming strategy'() {
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
                        )
        )
        // @formatter:on
        // Project name
        def projectName = uid('P')
        // Repository
        def git = GitRepo.prepare('custom-naming')
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}_GENERATOR")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}_GENERATOR", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_GENERATOR")
        // Fires the branch seed (extra timeout because of Gradle runtime download)
        jenkins.fireJob("${projectName}/${projectName}_MASTER/${projectName}_MASTER_GENERATOR", [:], 600).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD")
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_020_CI")
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_030_PUBLISH")
        // Checks the result of the pipeline (build, ci & publish must have been fired)
        jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_020_CI", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_030_PUBLISH", 1).checkSuccess()
    }

    @Test
    void 'Git branch'() {
        // Default seed
        String seed = jenkins.defaultSeed()
        // Project name
        def projectName = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('std', 'release/11.7')
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
                BRANCH: 'release/11.7'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-seed")
        // Fires the branch seed
        def output = jenkins.fireJob("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-seed").checkSuccess().output
        println "OUTPUT ${projectName}-release-11.7-seed:\n${output}"
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-build")
        jenkins.checkJobExists("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-ci")
        jenkins.checkJobExists("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-publish")
        // Checks the result of the pipeline (ci & publish must have been fired)
        jenkins.getBuild("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-build", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-ci", 1).checkSuccess()
        jenkins.getBuild("${projectName}/${projectName}-release-11.7/${projectName}-release-11.7-publish", 1).checkSuccess()
    }

    /**
     * The <code>copyPipelineDemo</code> Gradle task must be run before this test can run.
     */
    @Test
    void 'Pipeline library'() {
        // Project name
        def projectName = uid('P')
        // Creates a Git repository with a seed.properties file
        String userDir = System.getProperty('user.dir')
        String seedProperties = """\
seed.dsl.repository = flat:${userDir}/build/integration/repository
seed.dsl.libraries = :seed-pipeline-demo:+
seed.dsl.script.jar = seed-pipeline-demo
"""
        def git = GitRepo.prepare('custom', [
                'seed/seed.properties': seedProperties
        ])
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
                        )
                        .withEventStrategy(
                            new EventStrategyConfig().withAuto(false) // Launching the seed manually for specific timeout
                )
        )
        // @formatter:on
        // Firing the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : git,
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}_GENERATOR")
        // Fires the project seed
        jenkins.fireJob("${projectName}/${projectName}_GENERATOR", [
                BRANCH: 'master'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_GENERATOR")
        // Fires the branch seed (extra timeout because of Gradle runtime download)
        jenkins.fireJob("${projectName}/${projectName}_MASTER/${projectName}_MASTER_GENERATOR", [:], 600).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD")
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_020_CI")
        jenkins.checkJobExists("${projectName}/${projectName}_MASTER/${projectName}_MASTER_030_PUBLISH")
        // Check the downstream links
        jenkins.jobConfig("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD").with { xml ->
            def config = xml.publishers['hudson.plugins.parameterizedtrigger.BuildTrigger'].configs['hudson.plugins.parameterizedtrigger.BuildTriggerConfig']
            assert config.projects.text() == "${projectName}_MASTER_020_CI"
            assert config.condition.text() == 'SUCCESS'
            assert config.triggerWithNoParameters.text() == 'true'
        }
        jenkins.jobConfig("${projectName}/${projectName}_MASTER/${projectName}_MASTER_020_CI").with { xml ->
            def config = xml.publishers['hudson.plugins.parameterizedtrigger.BuildTrigger'].configs['hudson.plugins.parameterizedtrigger.BuildTriggerConfig']
            assert config.projects.text() == "${projectName}_MASTER_030_PUBLISH"
            assert config.condition.text() == 'SUCCESS'
            assert config.triggerWithNoParameters.text() == 'true'
        }
        // Fires the build job (not fired automatically by the custom library)
        jenkins.fireJob("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD")
        // Checks the result of the pipeline (build, ci & publish must have been fired)
        jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_010_BUILD", 1).checkSuccess()
//      TODO  jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_020_CI", 1).checkSuccess()
//      TODO  jenkins.getBuild("${projectName}/${projectName}_MASTER/${projectName}_MASTER_030_PUBLISH", 1).checkSuccess()
    }

    @Test
    void 'Branch parameter in the seed.groovy'() {
        // Default seed
        String seed = jenkins.defaultSeed()
        // Project name
        def projectName = uid('p')
        // Prepares Git repository
        def git = GitRepo.prepare('branch', 'feature/great')
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
                BRANCH: 'feature/great'
        ]).checkSuccess()
        // Checks the branch seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-feature-great/${projectName}-feature-great-seed")
        // The branch seed must have been fired
        jenkins.getBuild("${projectName}/${projectName}-feature-great/${projectName}-feature-great-seed", 1).checkSuccess()
        // Checks the branch pipeline is there
        jenkins.checkJobExists("${projectName}/${projectName}-feature-great/${projectName}-feature-great-build")
        // The branch pipeline must have been fired
        def output = jenkins.getBuild("${projectName}/${projectName}-feature-great/${projectName}-feature-great-build", 1).checkSuccess().output
        // Checks the branch name has been output
        assert output.contains("Project: ${projectName}")
        assert output.contains('Branch: feature/great')
        assert output.contains("Seed project: ${projectName}")
        assert output.contains('Seed branch: feature-great')
        assert output.contains('SCM type: git')
        assert output.contains("SCM URL: ${git}")
    }

    @Test
    void 'Custom location for the seed.groovy file'() {
        // Creates a seed job with a custom setting for the location
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withScriptDirectory("pipeline")
        )
        // Project name
        def projectName = uid('p')
        // Prepares Git repository with the seed.groovy in a custom directory
        Map<String, String> resources = [:]
        GitRepo.loadResource(resources, "seed-location-pipeline.groovy", "pipeline/seed.groovy")
        def git = GitRepo.prepare('location-pipeline', resources, 'master')
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
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
        // Checks the branch build is created
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-build")
    }

    @Test
    void 'Root directory for the seed.groovy file, using dot'() {
        // Creates a seed job with a custom setting for the location
        def seed = jenkins.seed(
                new PipelineConfig()
                        .withScriptDirectory(".")
        )
        // Project name
        def projectName = uid('p')
        // Prepares Git repository with the seed.groovy in a custom directory
        Map<String, String> resources = [:]
        GitRepo.loadResource(resources, "seed-location-dot.groovy", "seed.groovy")
        def git = GitRepo.prepare('location-dot', resources, 'master')
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
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
        // Checks the branch build is created
        jenkins.checkJobExists("${projectName}/${projectName}-master/${projectName}-master-build")
    }
}

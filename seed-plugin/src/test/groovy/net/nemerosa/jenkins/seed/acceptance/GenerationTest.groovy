package net.nemerosa.jenkins.seed.acceptance

import net.nemerosa.jenkins.seed.config.NamingStrategyConfig
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.test.AcceptanceTestRunner
import net.nemerosa.jenkins.seed.test.JenkinsAccessRule
import org.junit.Assert
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

    public static final int JOB_TIMEOUT = 1 * 60

    @Rule
    public JenkinsAccessRule jenkins = new JenkinsAccessRule()

    @Test
    void 'Default seed job created'() {
        jenkins.defaultSeed()
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

    // TODO Direct script execution?
    @Test
    @Ignore
    void 'Direct script execution - not allowed at project level'() {
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

    // TODO Direct script execution?
    @Test
    @Ignore
    void 'Direct script execution - allowed at project level'() {
        // Project name
        String project = uid('p')
        // Configuration of the Seed job
        jenkins.configureSeed '''\
pipeline-generator-script-allowed: no
classes:
    - id: my-class
      pipeline-generator-script-allowed: yes
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
        jenkins.fireJob("${project}/${project}-master/${project}-master-seed").checkSuccess()
    }

    @Test
    void 'Project folder authorisations'() {
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
                PROJECT         : 'test-auth',
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
                                // TODO Commit parameter? REVISION
                                // TODO Branch name prefixes? branches/
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
    void 'Repository credentials'() {
        // Project name
        String project = uid('p')
        // Configuration (default)
        jenkins.configureSeed ""
        // Firing the seed job
        jenkins.fireJob('seed', [
                PROJECT         : project,
                PROJECT_SCM_TYPE: 'git',
                // Path to the prepared Git repository in local-acceptance.gradle
                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-cred',
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
        // Checks some parameters of the master seed
        def xml = jenkins.jobConfig("${project}/${project}-master/${project}-master-seed")
        // Injection of passwords is enabled?
        assert xml?.buildWrappers?.EnvInjectPasswordWrapper?.injectGlobalPasswords?.text() == 'true'
        // Gets the build.gradle file from the workspace
        def buildGradle = jenkins.getWorkspaceFile("${project}/${project}-master/${project}-master-seed", "seed/build.gradle")
        // Careful: there are four spaces in the dependencies section
        Assert.assertEquals """\
repositories {
    maven {
    url 'https://artifactory.nemerosa.net'
    credentials {
        username System.getenv('ARTIFACTORY_USER')
        password System.getenv('ARTIFACTORY_PASSWORD')
    }
}

}
configurations {
    dslLibrary
}
dependencies {

}
task clean {
    delete 'lib'
}
task copyLibraries(type: Copy, dependsOn: clean) {
    into 'lib'
    from configurations.dslLibrary
}
task prepare(dependsOn: copyLibraries)
""", buildGradle
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
    @Ignore
    void 'Project pipeline extensions'() {
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
      project-seed-extensions:
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
        // Gets the project seed build...
        def projectSeedBuild = jenkins.getBuild("${project}/${project}-seed", 1)
        // ... gets its output
        def projectSeedBuildOutput = projectSeedBuild.output
        // ... and checks it contains the customisations
        assert projectSeedBuildOutput.contains('Extension 1')
        assert projectSeedBuildOutput.contains('Extension 2')
    }

    @Test
    @Ignore
    void 'Pipeline is fired by default after regeneration'() {
        // Project name
        String project = uid('p')
        // Default configuration
        jenkins.configureSeed ''
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
        // Checks the result of the pipeline (build must have been fired automatically)
        jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1).checkSuccess()
    }

    @Test
    @Ignore
    void 'Pipeline is not fired after regeneration when pipeline-start-auto is disabled'() {
        // Project name
        String project = uid('p')
        // Default configuration
        jenkins.configureSeed """
projects:
    - id: ${project}
      pipeline-start-auto: no
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
        // Checks the result of the pipeline (build must have been fired automatically)
        try {
            jenkins.getBuild("${project}/${project}-master/${project}-master-build", 1, 30).checkSuccess()
            fail "The pipeline should not have been fired"
        } catch (TimeoutException ignored) {
            // OK
        }
    }

    @Test
    @Ignore
    void 'Destructor job'() {
        // Project name
        String project = uid('p')
        // Default configuration
        jenkins.configureSeed """
projects:
    - id: ${project}
      project-destructor: yes
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
    @Ignore
    void 'Destructor job with custom naming convention'() {
        // Project name
        String project = uid('p')
        // Default configuration
        jenkins.configureSeed """
strategies:
  - id: custom
    seed-expression: "\${PROJECT}/\${PROJECT}_GENERATOR"
    destructor-expression: "\${PROJECT}/\${PROJECT}_DESTRUCTOR"
    branch-seed-expression: "\${PROJECT}/\${PROJECT}_*/\${PROJECT}_*_GENERATOR"
    branch-start-expression: "\${PROJECT}/\${PROJECT}_*/\${PROJECT}_*_010_BUILD"
    branch-name-expression: "\${BRANCH}"
    branch-name-prefixes:
      - "branches/"
    commit-parameter: "REVISION"
projects:
    - id: ${project}
      branch-strategy: custom
      project-destructor: yes
"""
        // Firing the seed job
        jenkins.fireJob('seed', [
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

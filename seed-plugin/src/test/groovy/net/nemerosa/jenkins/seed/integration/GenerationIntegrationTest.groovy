package net.nemerosa.jenkins.seed.integration

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
        jenkins.fireJob("${projectName}/${projectName}-master/${projectName}-master-seed").checkSuccess()
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
}

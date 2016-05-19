package net.nemerosa.jenkins.seed.integration

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
        // TODO Uses a GitRepoRule to have a configured repository
        // Fires the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                // TODO Path to the Git repo created by the GitRepoRule
                // PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
        ]).checkSuccess()
        // TODO Checks the project seed is created
//        // Checks the project seed is created
//        jenkins.job("${projectName}/${projectName}-seed")
    }
}

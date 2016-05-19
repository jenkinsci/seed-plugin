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
        // Fires the seed job
        jenkins.fireJob(seed, [
                PROJECT         : projectName,
                PROJECT_SCM_TYPE: 'git',
                PROJECT_SCM_URL : '',
        ]).checkSuccess()
        // Checks the project seed is created
        jenkins.checkJobExists("${projectName}/${projectName}-seed")
    }
}

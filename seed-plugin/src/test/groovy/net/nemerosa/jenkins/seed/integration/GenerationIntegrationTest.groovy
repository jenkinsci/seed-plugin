package net.nemerosa.jenkins.seed.integration

import org.junit.Rule
import org.junit.Test

class GenerationIntegrationTest {

    @Rule
    public SeedRule jenkins = new SeedRule();

    @Test
    void 'Project seed'() {
        // Creates a seed job
        def seed = jenkins.defaultSeed()
        // TODO Uses a GitRepoRule to have a configured repository
        // TODO Fires the seed job
        // TODO Checks the project seed is created
//        // Project name
//        def projectName = uid('p')
//        // Firing the seed job
//        jenkins.fireJob(seed, [
//                PROJECT         : projectName,
//                PROJECT_SCM_TYPE: 'git',
//                // Path to the prepared Git repository in docker.gradle
//                PROJECT_SCM_URL : '/var/lib/jenkins/tests/git/seed-std',
//        ]).checkSuccess()
//        // Checks the project seed is created
//        jenkins.job("${projectName}/${projectName}-seed")
    }
}

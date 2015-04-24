package net.nemerosa.seed.jenkins.step

import javaposse.jobdsl.dsl.*
import net.nemerosa.seed.jenkins.service.ExplicitBranchStrategiesLoader
import net.nemerosa.seed.jenkins.strategy.ExplicitBranchStrategies
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy
import net.nemerosa.seed.jenkins.support.SeedDSLHelper
import net.nemerosa.seed.jenkins.test.MockSeedConfigurationLoader
import org.junit.Test

class DSLLauncherTest {

    @Test
    void 'Calling simple script'() {
        // Project seed generation script
        String script = SeedDSLHelper.getResourceAsText("/project-seed-generator.groovy")

        // Jobs are created at the Jenkins root level
        JobManagement jm = new MemoryJobManagement(System.out)
        jm.getParameters().put('PROJECT', 'test')
        jm.getParameters().put('PROJECT_CLASS', '')
        jm.getParameters().put('PROJECT_SCM_TYPE', 'GIT')
        jm.getParameters().put('PROJECT_SCM_URL', 'test')

        // Generation request
        def seedDSLHelper = new SeedDSLHelper(
                new MockSeedConfigurationLoader(), // Auto config
                new ExplicitBranchStrategies(
                        new ExplicitBranchStrategiesLoader([
                                new SeedBranchStrategy()
                        ])
                )
        )

        ScriptRequest scriptRequest = new ScriptRequest(
                null,
                script,
                [DslClasspath.classpathFor(this.getClass())] as URL[],
                false, // not ignoring existing,
                Collections.<String, Object> singletonMap("seedDSLHelper", seedDSLHelper)
        );

        // Generation
        GeneratedItems generatedItems = DslScriptLoader.runDslEngine(
                scriptRequest,
                jm,
                getClass().getClassLoader()
        );

        // TODO Logging of generated items
        generatedItems.jobs.each { println "Job: ${it.jobName}" }
    }

}

package net.nemerosa.seed.jenkins.step

import javaposse.jobdsl.dsl.*
import net.nemerosa.seed.jenkins.support.SeedDSLHelper
import org.junit.Test

class DSLLauncherTest {

    @Test
    void 'Calling simple script'() {
        // Project seed generation script
        String script = SeedDSLHelper.getResourceAsText("/project-seed-generator.groovy")

        // Jobs are created at the Jenkins root level
        JobManagement jm = new MemoryJobManagement(System.out)
        jm.getParameters().put('PROJECT', 'test')

        // Generation request
        ScriptRequest scriptRequest = new ScriptRequest(
                null,
                script,
                [DslClasspath.classpathFor(this.getClass())] as URL[],
                false, // not ignoring existing,
                Collections.<String, Object> singletonMap("seedDSLHelper", new SeedDSLHelper())
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

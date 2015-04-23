package net.nemerosa.seed.jenkins.step

import javaposse.jobdsl.dsl.DslScriptLoader
import net.nemerosa.seed.jenkins.SeedPlugin
import org.junit.Test

import static net.nemerosa.seed.jenkins.step.DslClasspath.classpathFor

class DSLClasspathTest {

    @Test
    public void pluginClasspath() throws Exception {
        println classpathFor(SeedPlugin)
    }

    @Test
    public void jarPluginClasspath() throws Exception {
        println classpathFor(DslScriptLoader)
    }

}

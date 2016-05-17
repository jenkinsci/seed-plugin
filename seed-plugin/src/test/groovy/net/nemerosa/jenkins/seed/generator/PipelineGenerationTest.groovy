package net.nemerosa.jenkins.seed.generator

import hudson.model.BuildListener
import org.junit.Assert
import org.junit.Test

import static net.nemerosa.jenkins.seed.generator.PipelineGeneration.extractCredential
import static org.mockito.Mockito.mock

class PipelineGenerationTest {

    @Test
    void 'Extract credential from text'() {
        assert extractCredential("user") == "'user'"
    }

    @Test
    void 'Extract credential from env'() {
        assert extractCredential('${MY_USER}') == "System.getenv('MY_USER')"
    }

    @Test
    void 'Extract credential from incomplete expression'() {
        assert extractCredential('${MY_USER') == "'\${MY_USER'"
        assert extractCredential('{MY_USER}') == "'{MY_USER}'"
        assert extractCredential('test${MY_USER}') == "'test\${MY_USER}'"
        assert extractCredential('${MY_USER}test') == "'\${MY_USER}test'"
    }

    @Test
    void 'No repository credentials'() {
        def properties = new Properties()
        def dsl = PipelineGeneration.generateRepositoryGradle('https://github.com/nemerosa/ontrack', properties, mock(BuildListener))
        Assert.assertEquals "maven { url 'https://github.com/nemerosa/ontrack' }", dsl
    }

    @Test
    void 'Repository credentials'() {
        def properties = new Properties()
        properties['seed.dsl.repository.user'] = '${ARTIFACTORY_USER}'
        properties['seed.dsl.repository.password'] = '${ARTIFACTORY_PASSWORD}'
        def dsl = PipelineGeneration.generateRepositoryGradle('https://artifactory.nemerosa.net', properties, mock(BuildListener))
        Assert.assertEquals """\
maven {
    url 'https://artifactory.nemerosa.net'
    credentials {
        username System.getenv('ARTIFACTORY_USER')
        password System.getenv('ARTIFACTORY_PASSWORD')
    }
}
""", dsl
    }

}

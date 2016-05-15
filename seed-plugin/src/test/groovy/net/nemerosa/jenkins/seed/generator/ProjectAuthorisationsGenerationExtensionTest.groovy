package net.nemerosa.jenkins.seed.generator

import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.config.ProjectParameters
import org.junit.Test

class ProjectAuthorisationsGenerationExtensionTest {

    @Test
    void 'No authorisations by default'() {
        def parameters = new ProjectParameters(
                "test",
                "git",
                "https://github.com/nemerosa/ontrack.git",
                ""
        )
        PipelineConfig config = new PipelineConfig()
        String script = new ProjectAuthorisationsGenerationExtension(config, parameters).generate()
        assert script == ''
    }

    @Test
    void 'Authorisations'() {
        def parameters = new ProjectParameters(
                "test",
                "git",
                "https://github.com/nemerosa/ontrack.git",
                ""
        )
        PipelineConfig config = new PipelineConfig()
                .withAuthorisations('''\
                hudson.model.Item.Workspace:jenkins_*
                hudson.model.Item.Read:jenkins_*
                # Comments and empty lines are allowed

                hudson.model.Item.Discover:jenkins_*
                ''')
        String script = new ProjectAuthorisationsGenerationExtension(config, parameters).generate()
        assert script == '''\
authorization {
    permission('hudson.model.Item.Workspace:jenkins_test')
    permission('hudson.model.Item.Read:jenkins_test')
    permission('hudson.model.Item.Discover:jenkins_test')
}
'''
    }

}

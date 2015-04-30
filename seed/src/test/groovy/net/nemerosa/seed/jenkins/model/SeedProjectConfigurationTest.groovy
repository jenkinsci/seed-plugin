package net.nemerosa.seed.jenkins.model

import net.nemerosa.seed.config.SeedConfiguration
import net.nemerosa.seed.config.SeedProjectConfiguration
import org.junit.Test

import static org.junit.Assert.assertEquals

class SeedProjectConfigurationTest {

    @Test
    public void defaults() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of(Collections.singletonMap("id", "nemerosa/ontrack"));
        assertEquals("nemerosa/ontrack", c.getId());
        assertEquals("ontrack", c.getName());
    }

    @Test
    public void defaults_for_simple_project() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of("ontrack");
        assertEquals("ontrack", c.getId());
        assertEquals("ontrack", c.getName());
    }

    @Test
    void 'Getting project configuration through a class'() {
        def configuration = SeedConfiguration.parseYaml('''\
classes:
    - id: custom-auth
      authorisations:
          - hudson.model.Item.Workspace:jenkins_*
          - hudson.model.Item.Read:jenkins_*
          - hudson.model.Item.Discover:jenkins_*
          - hudson.model.Item.Read:jenkins_QA_*
          - hudson.model.Item.Discover:jenkins_QA_*
          - hudson.model.Item.Workspace:jenkins_*_BUILD
          - hudson.model.Item.Read:jenkins_*_BUILD
          - hudson.model.Item.Discover:jenkins_*_BUILD
          - hudson.model.Item.Cancel:jenkins_*_BUILD
          - hudson.model.Item.Build:jenkins_*_BUILD
      pipeline-generator: custom
projects:
    - id: test
      project-class: custom-auth
''')
        def project = configuration.getProjectConfiguration('test')
        def authorisations = project.getListString('authorisations')
        assert authorisations
        assert authorisations == [
                'hudson.model.Item.Workspace:jenkins_*',
                'hudson.model.Item.Read:jenkins_*',
                'hudson.model.Item.Discover:jenkins_*',
                'hudson.model.Item.Read:jenkins_QA_*',
                'hudson.model.Item.Discover:jenkins_QA_*',
                'hudson.model.Item.Workspace:jenkins_*_BUILD',
                'hudson.model.Item.Read:jenkins_*_BUILD',
                'hudson.model.Item.Discover:jenkins_*_BUILD',
                'hudson.model.Item.Cancel:jenkins_*_BUILD',
                'hudson.model.Item.Build:jenkins_*_BUILD',
        ]
        assert project.getString('pipeline-generator', false, 'seed') == 'custom'
    }

}

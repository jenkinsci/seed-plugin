package net.nemerosa.seed.config

import org.junit.Test

class SeedProjectEnvironmentTest {

    @Test
    void 'Parameters across projects, classes and general'() {
        SeedConfiguration configuration = SeedConfiguration.parseYaml('''\
branch-parameters:
    GEN: General variable
classes:
    - id: test-class
      branch-parameters:
        TEST: Test variable for the class
        CLS: Class variable
projects:
    - id: test
      project-class: test-class
      branch-parameters:
        TEST: Test variable for the project
''')
        def project = configuration.getProjectConfiguration('test')
        assert Configuration.getParameters('branch-parameters', project, configuration) == [
                GEN : 'General variable',
                CLS : 'Class variable',
                TEST: 'Test variable for the project',
        ]
    }

    @Test
    void 'Parameters for a project and a class'() {
        SeedConfiguration configuration = SeedConfiguration.parseYaml('''\
classes:
    - id: test-class
      branch-parameters:
        TEST: Test variable for the class
        CLS: Class variable
projects:
    - id: test
      branch-parameters:
        TEST: Test variable for the project
''')
        def project = configuration.getProjectConfiguration('test', 'test-class')
        assert Configuration.getParameters('branch-parameters', project, configuration) == [
                CLS : 'Class variable',
                TEST: 'Test variable for the project',
        ]
    }

    @Test
    void 'Parameters across projects and classes'() {
        SeedConfiguration configuration = SeedConfiguration.parseYaml('''\
classes:
    - id: test-class
      branch-parameters:
        TEST: Test variable for the class
        CLS: Class variable
projects:
    - id: test
      project-class: test-class
      branch-parameters:
        TEST: Test variable for the project
''')
        def project = configuration.getProjectConfiguration('test')
        assert Configuration.getParameters('branch-parameters', project, configuration) == [
                CLS : 'Class variable',
                TEST: 'Test variable for the project',
        ]
    }

    @Test
    void 'Parameters for a project'() {
        SeedConfiguration configuration = SeedConfiguration.parseYaml('''\
projects:
    - id: test
      branch-parameters:
        TEST: Test variable for the project
''')
        def project = configuration.getProjectConfiguration('test')
        assert Configuration.getParameters('branch-parameters', project, configuration) == [
                TEST: 'Test variable for the project'
        ]
    }

}

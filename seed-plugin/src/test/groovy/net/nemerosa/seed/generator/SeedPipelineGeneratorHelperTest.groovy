package net.nemerosa.seed.generator

import org.junit.Test

import static net.nemerosa.seed.generator.SeedPipelineGeneratorHelper.extractCredential

class SeedPipelineGeneratorHelperTest {

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

}

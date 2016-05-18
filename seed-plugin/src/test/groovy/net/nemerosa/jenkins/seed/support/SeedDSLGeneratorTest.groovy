package net.nemerosa.jenkins.seed.support

import net.nemerosa.jenkins.seed.config.PipelineConfig
import org.junit.Test

class SeedDSLGeneratorTest {

    @Test
    void 'Default seed DSL generation'() {
        def dsl = SeedDSLGenerator.seedDsl('test', new PipelineConfig())
        assert dsl.contains("commitParameter ''")
        assert dsl.contains("projectFolderPath ''")
        assert dsl.contains("projectSeedName ''")
        assert dsl.contains("projectDestructorName ''")
        assert dsl.contains("branchFolderPath ''")
        assert dsl.contains("branchSeedName ''")
        assert dsl.contains("branchStartName ''")
        assert dsl.contains("branchName ''")
        assert dsl.contains("ignoredBranchPrefixes ''")
        assert dsl.contains("triggerType '\${PROJECT_TRIGGER_TYPE}'")
        assert dsl.contains("triggerSecret '\${PROJECT_TRIGGER_SECRET}'")
    }

}

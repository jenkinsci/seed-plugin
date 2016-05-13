package net.nemerosa.jenkins.seed.config

import org.junit.Test

class PipelineConfigTest {

    @Test
    void 'Default config'() {
        def cfg = PipelineConfig.defaultConfig()
        assert cfg.authorisations == null
        assert cfg.branchParameters == null
        assert !cfg.branchSCMParameter
        assert cfg.commitParameter == null
        assert !cfg.destructor

        def strategy = cfg.namingStrategy
        assert strategy != null
        assert strategy.projectFolderPath == null
        assert strategy.projectSeedName == null
        assert strategy.projectDestructorName == null
        assert strategy.branchFolderPath == null
        assert strategy.branchSeedName == null
        assert strategy.branchStartName == null

        def eventStrategy = cfg.eventStrategy
        assert eventStrategy.auto
        assert eventStrategy.delete
        assert eventStrategy.commit == null
        assert eventStrategy.startAuto
    }

}

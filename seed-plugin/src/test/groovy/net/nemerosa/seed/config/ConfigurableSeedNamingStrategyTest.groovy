package net.nemerosa.seed.config

import org.junit.Test

class ConfigurableSeedNamingStrategyTest {

    @Test
    void 'SEED_BRANCH default'() {
        ConfigurableSeedNamingStrategy namingStrategy = new ConfigurableSeedNamingStrategy(
                ConfigurableBranchStrategyConfiguration.of([id: 'test'])
        )
        assert namingStrategy.getBranchName('release/1.0') == 'release-1.0'
    }

    @Test
    void 'SEED_BRANCH lower'() {
        ConfigurableSeedNamingStrategy namingStrategy = new ConfigurableSeedNamingStrategy(
                ConfigurableBranchStrategyConfiguration.of([id: 'test', 'branch-name-expression': '${branch}'])
        )
        assert namingStrategy.getBranchName('release/1.0') == 'release-1.0'
    }

    @Test
    void 'SEED_BRANCH upper'() {
        ConfigurableSeedNamingStrategy namingStrategy = new ConfigurableSeedNamingStrategy(
                ConfigurableBranchStrategyConfiguration.of([id: 'test', 'branch-name-expression': '${BRANCH}'])
        )
        assert namingStrategy.getBranchName('release/1.0') == 'RELEASE-1.0'
    }

    @Test
    void 'SEED_BRANCH upper underscore'() {
        ConfigurableSeedNamingStrategy namingStrategy = new ConfigurableSeedNamingStrategy(
                ConfigurableBranchStrategyConfiguration.of([id: 'test', 'branch-name-expression': '${BRANCH_}'])
        )
        assert namingStrategy.getBranchName('release/1.0') == 'RELEASE_1.0'
    }

    @Test
    void 'SEED_BRANCH capitalize'() {
        ConfigurableSeedNamingStrategy namingStrategy = new ConfigurableSeedNamingStrategy(
                ConfigurableBranchStrategyConfiguration.of([id: 'test', 'branch-name-expression': '${Branch}'])
        )
        assert namingStrategy.getBranchName('release/1.0') == 'Release-1.0'
    }

}

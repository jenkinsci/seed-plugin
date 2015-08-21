package net.nemerosa.seed.config

import org.junit.Test

class DefaultSeedNamingStrategyTest {

    @Test
    void 'SEED_BRANCH default'() {
        DefaultSeedNamingStrategy namingStrategy = new DefaultSeedNamingStrategy()
        assert namingStrategy.getBranchName('release/1.0') == 'release-1.0'
    }

}

package net.nemerosa.jenkins.seed.config

import org.junit.Test

class NamingStrategyConfigTest {

    @Test
    void 'No branch prefix'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
        assert config.getBranchName('test') == 'test'
        assert config.getBranchName('branches/release-1.0') == 'branches-release-1.0'
        assert config.getBranchName('feature/739-test') == 'feature-739-test'
    }

    @Test
    void 'One branch prefix'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
            .withIgnoredBranchPrefixes('branches/')
        assert config.getBranchName('test') == 'test'
        assert config.getBranchName('branches/release-1.0') == 'release-1.0'
        assert config.getBranchName('feature/739-test') == 'feature-739-test'
    }

    @Test
    void 'Two branch prefixes'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
            .withIgnoredBranchPrefixes('''\
                # Any branch
                branches/

                feature/
                ''')
        assert config.getBranchName('test') == 'test'
        assert config.getBranchName('branches/release-1.0') == 'release-1.0'
        assert config.getBranchName('feature/739-test') == '739-test'
    }

    @Test
    void 'Branch prefix with uppercase naming convention'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
            .withIgnoredBranchPrefixes('branches/')
            .withBranchName('${BRANCH}')
        assert config.getBranchName('test') == 'TEST'
        assert config.getBranchName('branches/release-1.0') == 'RELEASE-1.0'
        assert config.getBranchName('feature/739-test') == 'FEATURE-739-TEST'
    }

    @Test
    void 'Branch prefix with uppercase/underscore naming convention'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
            .withIgnoredBranchPrefixes('branches/')
            .withBranchName('${BRANCH_}')
        assert config.getBranchName('test') == 'TEST'
        assert config.getBranchName('branches/release-1.0') == 'RELEASE_1.0'
        assert config.getBranchName('feature/739-test') == 'FEATURE_739_TEST'
    }

}

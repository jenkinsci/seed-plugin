package net.nemerosa.jenkins.seed.config

import org.junit.Test

class NamingStrategyConfigTest {

    private final ProjectParameters parameters = new ProjectParameters(
            'seed-plugin',
            'git',
            'https://github.com/jenkinsci/seed-plugin',
            ''
    )

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

    @Test
    void 'Default branch folder path'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
        assert config.getBranchFolderPath(parameters, 'master') == 'seed-plugin-master'
        assert config.getBranchFolderPath(parameters, 'release/1.0') == 'seed-plugin-release-1.0'
    }

    @Test
    void 'Custom branch folder path'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
                .withBranchFolderPath('${PROJECT}_*')
                .withBranchName('${BRANCH}')
        assert config.getBranchFolderPath(parameters, 'master') == 'SEED-PLUGIN_MASTER'
        assert config.getBranchFolderPath(parameters, 'release/1.0') == 'SEED-PLUGIN_RELEASE-1.0'
    }

    @Test
    void 'Default branch seed name'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
        assert config.getBranchSeedName(parameters, 'master') == 'seed-plugin-master-seed'
        assert config.getBranchSeedName(parameters, 'release/1.0') == 'seed-plugin-release-1.0-seed'
    }

    @Test
    void 'Custom branch seed name'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
                .withBranchSeedName('${PROJECT}_*_GENERATOR')
                .withBranchName('${BRANCH}')
        assert config.getBranchSeedName(parameters, 'master') == 'SEED-PLUGIN_MASTER_GENERATOR'
        assert config.getBranchSeedName(parameters, 'release/1.0') == 'SEED-PLUGIN_RELEASE-1.0_GENERATOR'
    }

    @Test
    void 'Default branch start name'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
        assert config.getBranchStartName(parameters, 'master') == 'seed-plugin-master-build'
        assert config.getBranchStartName(parameters, 'release/1.0') == 'seed-plugin-release-1.0-build'
    }

    @Test
    void 'Custom branch start name'() {
        NamingStrategyConfig config = new NamingStrategyConfig()
                .withBranchStartName('${PROJECT}_*_01_CHECK')
                .withBranchName('${BRANCH}')
        assert config.getBranchStartName(parameters, 'master') == 'SEED-PLUGIN_MASTER_01_CHECK'
        assert config.getBranchStartName(parameters, 'release/1.0') == 'SEED-PLUGIN_RELEASE-1.0_01_CHECK'
    }

}

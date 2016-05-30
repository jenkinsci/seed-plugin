package net.nemerosa.jenkins.seed.integration.svn

import org.junit.Test

class SVNRepoTest {

    @Test
    void 'SVN configuration test'() {
        SVNRepo.withPreparedSvnRepo('test', 'branches/test', 'svn') { SVNRepo svn ->
            def dir = svn.checkout('test/branches/test/seed')
            def seed = new File(dir, 'seed.groovy')
            assert seed.exists()
            assert seed.text.contains('queue("${SEED_PROJECT}-${SEED_BRANCH}-build")')
        }
    }

}

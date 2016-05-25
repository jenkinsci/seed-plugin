package net.nemerosa.jenkins.seed.integration.git

import org.eclipse.jgit.api.Git
import org.junit.Test

/**
 * Tests the Git test repository...
 */
class GitRepoTest {

    @Test
    void 'Test Git repository'() {
        def dir = GitRepo.prepare('std')
        // Opens the repository just being created
        def git = Git.open(new File(dir))
        // Executes a log command
        def commits = git.log().all().call().iterator()
        assert commits.hasNext()
        def commit = commits.next()
        assert commit.fullMessage == "Seed files"
    }

}

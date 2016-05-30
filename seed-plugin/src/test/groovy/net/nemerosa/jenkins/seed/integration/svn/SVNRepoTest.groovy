package net.nemerosa.jenkins.seed.integration.svn

import org.junit.Test

class SVNRepoTest {

    @Test
    void 'SVN configuration test'() {
        SVNRepo.withPreparedSvnRepo('test', 'branches/test', 'custom') { SVNRepo svn ->
            def dir = svn.checkout('test/branches/test/seed')
            def seed = new File(dir, 'seed.properties')
            assert seed.exists()
            assert seed.text == '''\
# Configuration file for the 'Creating a project tree based of full customisation' acceptance test

# Flat repository for the demo
seed.dsl.repository = flat:/var/test/repository

# Comma-separated list of DSL libraries (Gradle dependency notation)
seed.dsl.libraries = :seed-pipeline-demo:+

# JAR which contains the bootstrap script
seed.dsl.script.jar = seed-pipeline-demo

# Location of the script in the JAR
# seed.dsl.script.location = seed.groovy
'''
        }
    }

}

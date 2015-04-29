package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

interface PipelineGenerator {

    String generate(SeedProjectEnvironment environment, String branch)

}
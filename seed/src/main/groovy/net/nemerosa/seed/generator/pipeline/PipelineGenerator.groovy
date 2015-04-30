package net.nemerosa.seed.generator.pipeline

import net.nemerosa.seed.config.SeedProjectEnvironment

interface PipelineGenerator {

    String generate(SeedProjectEnvironment environment, String branch)

}
package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

interface PipelineGenerator {

    void steps(def dsl, SeedProjectEnvironment env)

}
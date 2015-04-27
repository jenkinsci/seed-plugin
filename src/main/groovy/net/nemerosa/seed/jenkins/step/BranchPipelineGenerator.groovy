package net.nemerosa.seed.jenkins.step

import net.nemerosa.seed.jenkins.pipeline.PipelineGenerator
import net.nemerosa.seed.jenkins.pipeline.PipelineGeneratorNotFoundException
import net.nemerosa.seed.jenkins.pipeline.SeedPipelineGenerator
import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class BranchPipelineGenerator {

    // TODO Use IOC
    private static final Map<String, PipelineGenerator> pipelineGenerators = [
            seed: new SeedPipelineGenerator()
    ]

    private final SeedProjectEnvironment projectEnvironment
    private final String branch

    BranchPipelineGenerator(SeedProjectEnvironment projectEnvironment, String branch) {
        this.branch = branch
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        // Gets the pipeline generator ID
        String pipelineGeneratorId = projectEnvironment.getConfigurationValue('pipeline-generator', 'seed')
        // Gets the pipeline generator
        // TODO Uses IOC
        PipelineGenerator pipelineGenerator = pipelineGenerators[pipelineGeneratorId]
        if (pipelineGenerator) {
            return pipelineGenerator.generate(projectEnvironment, branch)
        } else {
            throw new PipelineGeneratorNotFoundException(pipelineGeneratorId)
        }
    }
}

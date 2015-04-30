package net.nemerosa.seed.generator

import net.nemerosa.seed.generator.pipeline.PipelineGenerator
import net.nemerosa.seed.generator.pipeline.PipelineGeneratorNotFoundException
import net.nemerosa.seed.generator.pipeline.PropertiesPipelineGenerator
import net.nemerosa.seed.generator.pipeline.SeedPipelineGenerator
import net.nemerosa.seed.config.SeedProjectEnvironment

class BranchPipelineGeneratorExtension {

    // TODO Use IOC
    private static final Map<String, PipelineGenerator> pipelineGenerators = [
            seed      : new SeedPipelineGenerator(),
            properties: new PropertiesPipelineGenerator(),
    ]

    private final SeedProjectEnvironment projectEnvironment
    private final String branch

    BranchPipelineGeneratorExtension(SeedProjectEnvironment projectEnvironment, String branch) {
        this.projectEnvironment = projectEnvironment
        this.branch = branch
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

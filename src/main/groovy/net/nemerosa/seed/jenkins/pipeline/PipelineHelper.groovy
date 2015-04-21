package net.nemerosa.seed.jenkins.pipeline

import net.nemerosa.seed.jenkins.model.Configuration
import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class PipelineHelper {

    // TODO Use IOC
    private static final Map<String, PipelineGenerator> pipelineGenerators = [
            seed: new SeedPipelineGenerator()
    ]

    /**
     * Generates the steps used to generate a branch pipeline
     */
    static void pipelineGenerationSteps(def dsl, SeedProjectEnvironment env) {
        // Gets the pipeline generator ID
        String pipelineGeneratorId = Configuration.getValue(
                'pipeline-generator',
                env.projectConfiguration,
                env.globalConfiguration,
                'seed'
        )
        // Gets the pipeline generator
        // TODO Uses IOC
        PipelineGenerator pipelineGenerator = pipelineGenerators[pipelineGeneratorId]
        if (pipelineGenerator) {
            pipelineGenerator.steps(dsl, env)
        } else {
            throw new PipelineGeneratorNotFoundException(pipelineGeneratorId)
        }
    }

}

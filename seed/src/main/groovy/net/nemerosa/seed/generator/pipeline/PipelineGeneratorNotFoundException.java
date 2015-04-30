package net.nemerosa.seed.generator.pipeline;

import net.nemerosa.seed.config.SeedException;

public class PipelineGeneratorNotFoundException extends SeedException {
    public PipelineGeneratorNotFoundException(String id) {
        super("Pipeline generator %s is not defined.", id);
    }
}

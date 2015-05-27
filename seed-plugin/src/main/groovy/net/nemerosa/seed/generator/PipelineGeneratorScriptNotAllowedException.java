package net.nemerosa.seed.generator;

import net.nemerosa.seed.config.SeedException;

public class PipelineGeneratorScriptNotAllowedException extends SeedException {
    public PipelineGeneratorScriptNotAllowedException() {
        super("Direct script execution is not allowed for this project and must be explicitly allowed " +
                "using the 'pipeline-generator-script-allowed' configuration parameter.");
    }
}

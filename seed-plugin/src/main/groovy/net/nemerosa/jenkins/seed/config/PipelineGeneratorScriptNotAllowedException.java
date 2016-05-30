package net.nemerosa.jenkins.seed.config;

import net.nemerosa.seed.config.SeedException;

public class PipelineGeneratorScriptNotAllowedException extends SeedException {
    public PipelineGeneratorScriptNotAllowedException() {
        super("Direct script execution is not allowed for this type of project.");
    }
}

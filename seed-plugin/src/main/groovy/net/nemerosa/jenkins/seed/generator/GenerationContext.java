package net.nemerosa.jenkins.seed.generator;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import lombok.Data;

import java.util.Map;

@Data
public class GenerationContext {

    /**
     * Environment variables
     */
    private final Map<String, String> environment;

    /**
     * Replacement functions
     */
    private final Map<String, GenerationExtension> extensions;

    /**
     * Post processing task
     */
    private final GenerationPostProcessing postProcessing;

    public void postProcessing(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env) {
        postProcessing.run(build, listener, env);
    }
}

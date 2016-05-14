package net.nemerosa.jenkins.seed.generator;

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

}

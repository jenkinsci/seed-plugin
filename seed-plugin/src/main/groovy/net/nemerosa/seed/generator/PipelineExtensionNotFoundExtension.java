package net.nemerosa.seed.generator;

import net.nemerosa.seed.config.SeedException;

public class PipelineExtensionNotFoundExtension extends SeedException {
    public PipelineExtensionNotFoundExtension(String id) {
        super(
                String.format(
                        "No extension with ID '%s' has been found. This extension should be declared " +
                                "in the 'pipeline-extensions' collection of the project or the configuration.",
                        id
                )
        );
    }
}

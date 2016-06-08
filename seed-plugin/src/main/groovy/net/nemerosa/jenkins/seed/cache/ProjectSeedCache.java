package net.nemerosa.jenkins.seed.cache;

/**
 * Cache for the project seed configurations, fed by the project seed generators and used
 * by the triggers.
 */
public interface ProjectSeedCache {

    /**
     * Gets the cached configuration for the project, or a default one if none could
     * be found.
     *
     * @param project The project identifier. This can be either the
     *                {@linkplain net.nemerosa.jenkins.seed.config.ProjectPipelineConfig#triggerIdentifier trigger identifier}
     *                or the {@linkplain net.nemerosa.jenkins.seed.config.ProjectPipelineConfig#project project name}.
     * @return Cached configuration or default one (never <code>null</code>).
     */
    ProjectCachedConfig getProjectPipelineConfig(String project);

}

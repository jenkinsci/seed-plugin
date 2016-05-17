package net.nemerosa.jenkins.seed.cache;

/**
 * Cache for the project seed configurations, fed by the project seed generators and used
 * by the triggers.
 */
public interface ProjectSeedCache {

    ProjectCachedConfig getProjectPipelineConfig(String project);

}

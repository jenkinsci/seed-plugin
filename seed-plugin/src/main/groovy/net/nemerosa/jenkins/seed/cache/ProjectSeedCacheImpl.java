package net.nemerosa.jenkins.seed.cache;

import jenkins.model.Jenkins;
import net.nemerosa.jenkins.seed.config.PipelineConfig;

public class ProjectSeedCacheImpl implements ProjectSeedCache {

    @Override
    public ProjectCachedConfig getProjectPipelineConfig(String project) {
        ProjectSeedCacheDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ProjectSeedCacheDescriptor.class);
        // Gets the project seed
        PipelineConfig seed = descriptor.getProjectSavedConfiguration(project);
        if (seed != null) {
            return new ProjectCachedConfig(
                    project,
                    seed
            );
        }
        // Not found
        return null;
    }

}

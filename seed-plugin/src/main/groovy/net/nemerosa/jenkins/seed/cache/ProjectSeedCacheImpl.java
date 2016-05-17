package net.nemerosa.jenkins.seed.cache;

import jenkins.model.Jenkins;

public class ProjectSeedCacheImpl implements ProjectSeedCache {

    @Override
    public ProjectCachedConfig getProjectPipelineConfig(String project) {
        ProjectSeedCacheDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ProjectSeedCacheDescriptor.class);
        // Gets the project seed
        return descriptor.getProjectSavedConfiguration(project);
    }

}

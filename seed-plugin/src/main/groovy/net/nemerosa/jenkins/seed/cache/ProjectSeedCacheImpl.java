package net.nemerosa.jenkins.seed.cache;

import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import net.nemerosa.jenkins.seed.generator.PipelineConfigAction;

public class ProjectSeedCacheImpl implements ProjectSeedCache {

    @Override
    public ProjectCachedConfig getProjectPipelineConfig(String project) {
        ProjectSeedCacheDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ProjectSeedCacheDescriptor.class);
        // Gets the project seed
        ProjectSeedCacheDescriptor.ProjectSeed seed = descriptor.getProjectSavedConfiguration(project);
        if (seed != null) {
            // Gets the project folder
            TopLevelItem folder = Jenkins.getInstance().getItem(seed.getFolderPath());
            if (folder != null) {
                Item seedJob = Jenkins.getInstance().getItem(seed.getSeedJob(), folder);
                if (seedJob instanceof AbstractProject) {
                    AbstractProject seedProject = (AbstractProject) seedJob;
                    PipelineConfigAction action = seedProject.getAction(PipelineConfigAction.class);
                    if (action != null) {
                        return new ProjectCachedConfig(
                                project,
                                action.getPipelineConfig()
                        );
                    }
                }
            }
        }
        // Not found
        return null;
    }

}

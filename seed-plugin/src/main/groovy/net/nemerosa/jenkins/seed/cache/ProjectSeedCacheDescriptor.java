package net.nemerosa.jenkins.seed.cache;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.model.Jenkins;
import lombok.Data;
import net.nemerosa.jenkins.seed.config.PipelineConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Descriptor used to hold the index of all projects and their associated configuration.
 */
@Extension
public class ProjectSeedCacheDescriptor extends Descriptor<ProjectSeedCacheDescriptor> implements Describable<ProjectSeedCacheDescriptor> {

    /**
     * Indexed data
     */
    @Data
    public static class ProjectSeed {
        private final String folderPath;
        private final String seedJob;
    }

    /**
     * Index of project configurations
     */
    private Map<String, ProjectSeed> projectSeeds = new HashMap<>();

    public ProjectSeedCacheDescriptor() {
        super(ProjectSeedCacheDescriptor.class);
        load();
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public Descriptor<ProjectSeedCacheDescriptor> getDescriptor() {
        return this;
    }

    public void saveProjectConfiguration(String project, PipelineConfig config) {
        projectSeeds.put(
                project,
                new ProjectSeed(
                        config.getProjectFolder(project),
                        config.getProjectSeedJob(project)
                )
        );
        save();
    }

    private static void removeProjectConfiguration(String name) {
        ProjectSeedCacheDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ProjectSeedCacheDescriptor.class);
        ProjectSeed removed = descriptor.projectSeeds.remove(name);
        if (removed != null) {
            descriptor.save();
        }
    }

    public ProjectSeed getProjectSavedConfiguration(String projectName) {
        return projectSeeds.get(projectName);
    }

    @Extension
    public static class GeneratedJobMapItemListener extends ItemListener {

        @Override
        public void onDeleted(Item item) {
            removeProjectConfiguration(item.getFullName());
        }

        @Override
        public void onLocationChanged(Item item, String oldFullName, String newFullName) {
            removeProjectConfiguration(oldFullName);
        }

    }
}

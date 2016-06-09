package net.nemerosa.jenkins.seed.cache;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.model.Jenkins;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectSeed;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Descriptor used to hold the index of all projects and their associated configuration.
 */
@Extension
public class ProjectSeedCacheDescriptor extends Descriptor<ProjectSeedCacheDescriptor> implements Describable<ProjectSeedCacheDescriptor> {

    /**
     * Index of project configurations
     */
    private Map<String, ProjectCachedConfig> projectSeeds = new HashMap<>();

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

    public void saveProjectConfiguration(ProjectParameters parameters, PipelineConfig config) {
        projectSeeds.put(
                getProjectTriggerIdentifierOrName(parameters),
                new ProjectCachedConfig(
                        new ProjectSeed(parameters),
                        config
                )
        );
        save();
    }

    private static void removeProjectConfiguration(String name) {
        ProjectSeedCacheDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ProjectSeedCacheDescriptor.class);
        ProjectCachedConfig removed = descriptor.projectSeeds.remove(name);
        if (removed != null) {
            descriptor.save();
        }
    }

    /**
     * Gets the cached configuration for the project, or <code>null</code> if none could
     * be found.
     *
     * @param projectName The project identifier. This can be either the
     *                    {@linkplain net.nemerosa.jenkins.seed.config.ProjectPipelineConfig#triggerIdentifier trigger identifier}
     *                    or the {@linkplain net.nemerosa.jenkins.seed.config.ProjectPipelineConfig#project project name}.
     * @return Cached configuration or <code>null</code>.
     */
    public ProjectCachedConfig getProjectSavedConfiguration(String projectName) {
        return projectSeeds.get(projectName);
    }

    /**
     * Gets the trigger identifier if present, and if not, the project name
     */
    public String getProjectTriggerIdentifierOrName(ProjectParameters parameters) {
        String id = parameters.getTriggerIdentifier();
        if (StringUtils.isNotBlank(id)) {
            return id;
        } else {
            return parameters.getProject();
        }
    }


    @Extension
    public static class ProjectSeedCacheDescriptorItemListener extends ItemListener {

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

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Descriptor used to hold the index of all projects and their associated configuration.
 */
@Extension
public class ProjectSeedCacheDescriptor extends Descriptor<ProjectSeedCacheDescriptor> implements Describable<ProjectSeedCacheDescriptor> {

    private static final Logger logger = Logger.getLogger(ProjectSeedCacheDescriptor.class.getName());

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
                parameters.getProject(),
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
        AtomicReference<ProjectCachedConfig> cache = new AtomicReference<>();
        for (ProjectCachedConfig config : projectSeeds.values()) {
            if (matchConfig(config, projectName)) {
                if (cache.get() != null) {
                    logger.warning(
                            String.format(
                                    "[seed][cache] Project configuration for %s already cached with name=%s, id=%s. Consider filling the trigger identifier value to solve conflicts.",
                                    projectName,
                                    cache.get().getSeed().getProject(),
                                    cache.get().getSeed().getTriggerIdentifier()
                            )
                    );
                } else {
                    cache.set(config);
                }
            }
        }
        return cache.get();
    }

    private boolean matchConfig(ProjectCachedConfig config, String projectName) {
        return StringUtils.equals(config.getSeed().getTriggerIdentifier(), projectName) ||
                StringUtils.equals(config.getSeed().getProject(), projectName);
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

package net.nemerosa.seed;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.model.Jenkins;
import net.nemerosa.seed.config.SeedProjectEnvironment;
import net.nemerosa.seed.config.SeedProjectSavedConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Descriptor used to hold the index of all projects and their associated configuration.
 */
@Extension
public class SeedDescriptor extends Descriptor<SeedDescriptor> implements Describable<SeedDescriptor> {

    /**
     * Index of project configurations
     */
    private Map<String, SeedProjectSavedConfiguration> projectConfigurations = new HashMap<String, SeedProjectSavedConfiguration>();

    public SeedDescriptor() {
        super(SeedDescriptor.class);
        load();
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public Descriptor<SeedDescriptor> getDescriptor() {
        return this;
    }

    public void saveProjectConfiguration(SeedProjectEnvironment projectEnvironment) {
        SeedProjectSavedConfiguration configuration = new SeedProjectSavedConfiguration(projectEnvironment);
        projectConfigurations.put(projectEnvironment.getId(), configuration);
        save();
    }

    private static void removeProjectConfiguration(String name) {
        SeedDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(SeedDescriptor.class);
        SeedProjectSavedConfiguration removed = descriptor.projectConfigurations.remove(name);
        if (removed != null) {
            descriptor.save();
        }
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

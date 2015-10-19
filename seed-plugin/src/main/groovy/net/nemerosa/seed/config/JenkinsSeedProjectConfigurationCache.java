package net.nemerosa.seed.config;

import jenkins.model.Jenkins;
import net.nemerosa.seed.SeedDescriptor;

public class JenkinsSeedProjectConfigurationCache implements SeedProjectConfigurationCache {

    @Override
    public SeedProjectSavedConfiguration load(String projectName) {
        SeedDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(SeedDescriptor.class);
        return descriptor.getProjectSavedConfiguration(projectName);
    }

}

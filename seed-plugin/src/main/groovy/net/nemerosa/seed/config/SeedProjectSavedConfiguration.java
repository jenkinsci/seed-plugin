package net.nemerosa.seed.config;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

public class SeedProjectSavedConfiguration {

    private final String id;
    private final String projectClass;
    private final String scmType;
    private final String scmUrl;
    private final String scmCredentials;
    private final Map<String, ?> globalConfiguration;
    private final Map<String, ?> projectConfiguration;

    @DataBoundConstructor
    public SeedProjectSavedConfiguration(String id, String projectClass, String scmType, String scmUrl, String scmCredentials, Map<String, ?> globalConfiguration, Map<String, ?> projectConfiguration) {
        this.id = id;
        this.projectClass = projectClass;
        this.scmType = scmType;
        this.scmUrl = scmUrl;
        this.scmCredentials = scmCredentials;
        this.globalConfiguration = globalConfiguration;
        this.projectConfiguration = projectConfiguration;
    }

    public SeedProjectSavedConfiguration(SeedProjectEnvironment e) {
        this(
                e.getId(),
                e.getProjectClass(),
                e.getScmType(),
                e.getScmUrl(),
                e.getScmCredentials(),
                e.getGlobalConfiguration().getData(),
                e.getProjectConfiguration().getData()
        );
    }

    public String getId() {
        return id;
    }

    public String getProjectClass() {
        return projectClass;
    }

    public String getScmType() {
        return scmType;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public String getScmCredentials() {
        return scmCredentials;
    }

    public Map<String, ?> getGlobalConfiguration() {
        return globalConfiguration;
    }

    public Map<String, ?> getProjectConfiguration() {
        return projectConfiguration;
    }

    public boolean sameAs(String projectClass, String scmType, String scmUrl, String scmCredentials) {
        return StringUtils.equals(this.projectClass, projectClass) &&
                StringUtils.equals(this.scmType, scmType) &&
                StringUtils.equals(this.scmUrl, scmUrl) &&
                StringUtils.equals(this.scmCredentials, scmCredentials)
                ;
    }
}

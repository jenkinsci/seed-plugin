package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import lombok.experimental.Builder;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PipelineConfig {

    /**
     * Boolean to enable the creation of a destructor job.
     */
    private final boolean destructor;

    /**
     * Parameter to pass to the branch start
     */
    private final String commitParameter;

    /**
     * Line separated list of authorisations for a project
     */
    private final String authorisations;

    /**
     * Boolean which configures the project generator to have an extra BRANCH_SCM parameter
     */
    private final boolean branchSCMParameter;

    /**
     * Additional parameters to define.
     */
    private final String branchParameters;

    /**
     * Arbitrary DSL to add to the project generator job.
     */
    private final String generationExtension;

    /**
     * Naming strategy
     */
    private final NamingStrategyConfig namingStrategy;

    /**
     * Events configurations
     */
    private final EventStrategyConfig eventStrategy;

    @DataBoundConstructor
    public PipelineConfig(boolean destructor, String commitParameter, String authorisations, boolean branchSCMParameter, String branchParameters, String generationExtension, NamingStrategyConfig namingStrategy, EventStrategyConfig eventStrategy) {
        this.destructor = destructor;
        this.commitParameter = commitParameter;
        this.authorisations = authorisations;
        this.branchSCMParameter = branchSCMParameter;
        this.branchParameters = branchParameters;
        this.generationExtension = generationExtension;
        this.namingStrategy = namingStrategy;
        this.eventStrategy = eventStrategy;
    }

    public String getProjectFolder(ProjectParameters parameters) {
        return namingStrategy.getProjectFolder(parameters);
    }

    public String getProjectSeedJob(ProjectParameters parameters) {
        return namingStrategy.getProjectSeedJob(parameters);
    }

    public String getProjectDestructorJob(ProjectParameters parameters) {
        return namingStrategy.getProjectDestructorJob(parameters);
    }

    public List<String> getProjectAuthorisations(ProjectParameters parameters) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(authorisations)) {
            String[] array = StringUtils.split(authorisations, "\n");
            for (String line : array) {
                line = StringUtils.trim(line);
                if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
                    String token = StringUtils.replace(line, "*", parameters.getProject());
                    list.add(token);
                }
            }
        }
        return list;
    }

    /**
     * Gets the extra parameters for the creation of a branch
     *
     * @param parameters Resolved project parameters
     * @return List of extra parameters (name : description)
     */
    public Map<String, String> getBranchParameters(ProjectParameters parameters) {
        Map<String, String> list = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(branchParameters)) {
            String[] array = StringUtils.split(branchParameters, "\n");
            for (String line : array) {
                line = StringUtils.trim(line);
                if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
                    String token = StringUtils.replace(line, "*", parameters.getProject());
                    String[] attrs = StringUtils.split(token, ":");
                    if (attrs.length == 2) {
                        list.put(attrs[0].trim(), attrs[1].trim());
                    }
                }
            }
        }
        return list;
    }

    /**
     * Default configuration
     */
    public static PipelineConfig defaultConfig() {
        return PipelineConfig.builder().build();
    }
}

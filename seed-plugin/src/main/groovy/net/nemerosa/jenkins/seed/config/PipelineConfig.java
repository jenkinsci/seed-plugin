package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import lombok.experimental.Wither;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class PipelineConfig {

    /**
     * Boolean to enable the creation of a destructor job.
     */
    @Wither
    private final boolean destructor;

    /**
     * Line separated list of authorisations for a project
     */
    @Wither
    private final String authorisations;

    /**
     * Boolean which configures the project generator to have an extra BRANCH_SCM parameter
     */
    @Wither
    private final boolean branchSCMParameter;

    /**
     * Additional parameters to define.
     */
    @Wither
    private final String branchParameters;

    /**
     * Arbitrary DSL to add to the project generator job.
     */
    @Wither
    private final String generationExtension;

    /**
     * Arbitrary DSL to add to the pipeline generation job.
     */
    @Wither
    private final String pipelineGenerationExtension;

    /**
     * Disables the execution of arbitrary DSL Groovy scripts - relies on
     * pipeline libraries.
     */
    @Wither
    private final boolean disableDslScript;

    /**
     * Path to the directory which contains the pipeline script. Defaults to <code>seed</code> if not set.
     */
    @Wither
    private final String scriptDirectory;

    /**
     * Naming strategy
     */
    @Wither
    private final NamingStrategyConfig namingStrategy;

    /**
     * Events configurations
     */
    @Wither
    private final EventStrategyConfig eventStrategy;

    public PipelineConfig() {
        this(
                false, // No destructor by default
                "", // No authorisations
                false, // No branch SCM parameter
                "", // No extra parameter
                "", // No extra DSL
                "", // No extra DSL
                false, // Allows DSL script
                "", // Using default
                new NamingStrategyConfig(), // Default values
                new EventStrategyConfig() // Default values
        );
    }

    /**
     * Default constructor with default values
     */
    @DataBoundConstructor
    public PipelineConfig(boolean destructor, String authorisations, boolean branchSCMParameter, String branchParameters, String generationExtension, String pipelineGenerationExtension, boolean disableDslScript, String scriptDirectory, NamingStrategyConfig namingStrategy, EventStrategyConfig eventStrategy) {
        this.destructor = destructor;
        this.authorisations = authorisations;
        this.branchSCMParameter = branchSCMParameter;
        this.branchParameters = branchParameters;
        this.generationExtension = generationExtension;
        this.pipelineGenerationExtension = pipelineGenerationExtension;
        this.disableDslScript = disableDslScript;
        this.scriptDirectory = scriptDirectory;
        this.namingStrategy = namingStrategy;
        this.eventStrategy = eventStrategy;
    }

    public String getProjectFolder(String project) {
        return namingStrategy.getProjectFolder(project);
    }

    public String getProjectSeedJob(String project) {
        return namingStrategy.getProjectSeedJob(project);
    }

    public String getProjectDestructorJob(String project) {
        return namingStrategy.getProjectDestructorJob(project);
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
        List<String> lines = LineParser.parseLines(branchParameters);
        Map<String, String> list = new LinkedHashMap<>();
        for (String line : lines) {
            String token = StringUtils.replace(line, "*", parameters.getProject());
            String[] attrs = StringUtils.split(token, ":");
            if (attrs.length == 2) {
                list.put(attrs[0].trim(), attrs[1].trim());
            }
        }
        return list;
    }

    /**
     * Default configuration
     */
    public static PipelineConfig defaultConfig() {
        return new PipelineConfig();
    }

    public String getBranchFolderPath(String project, String branch) {
        return namingStrategy.getBranchFolderPath(project, branch);
    }

    public String getBranchSeedName(String project, String branch) {
        return namingStrategy.getBranchSeedName(project, branch);
    }

    public String getBranchStartName(String project, String branch) {
        return namingStrategy.getBranchStartName(project, branch);
    }

    public String getBranchName(String branch) {
        return namingStrategy.getBranchName(branch);
    }
}

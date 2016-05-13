package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import lombok.experimental.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import static net.nemerosa.jenkins.seed.support.Evaluator.evaluate;

@Data
@Builder
public class NamingStrategyConfig {

    /**
     * Path to the project folder
     */
    private final String projectFolderPath;

    /**
     * Path to the branch folder
     */
    private final String branchFolderPath;

    /**
     * Name of the project seed
     */
    private final String projectSeedName;

    /**
     * Name of the project destructor
     */
    private final String projectDestructorName;

    /**
     * Name of the branch seed
     */
    private final String branchSeedName;

    /**
     * Start job name for the branch
     */
    private final String branchStartName;

    @DataBoundConstructor
    public NamingStrategyConfig(String projectFolderPath, String branchFolderPath, String projectSeedName, String branchSeedName, String branchStartName, String projectDestructorName) {
        this.projectFolderPath = projectFolderPath;
        this.branchFolderPath = branchFolderPath;
        this.projectSeedName = projectSeedName;
        this.projectDestructorName = projectDestructorName;
        this.branchSeedName = branchSeedName;
        this.branchStartName = branchStartName;
    }

    public String getProjectFolder(ProjectParameters parameters) {
        return evaluate(projectFolderPath, "project", parameters.getProject());
    }

    public String getProjectSeedJob(ProjectParameters parameters) {
        return evaluate(projectSeedName, "project", parameters.getProject());
    }

    public String getProjectDestructorJob(ProjectParameters parameters) {
        return evaluate(projectDestructorName, "project", parameters.getProject());
    }
}

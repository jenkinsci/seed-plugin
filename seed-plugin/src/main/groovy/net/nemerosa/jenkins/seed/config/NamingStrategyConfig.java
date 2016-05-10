package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import org.kohsuke.stapler.DataBoundConstructor;

@Data
public class NamingStrategyConfig {

    /**
     * Path to the project folder
     */
    private final String projectFolderPath/* = "${project}"*/;

    /**
     * Path to the branch folder
     */
    private final String branchFolderPath/* = "${branch}"*/;

    /**
     * Name of the project seed
     */
    private final String projectSeedName/* = "${project}-seed"*/;

    /**
     * Name of the project destructor
     */
    private final String projectDestructorName;

    /**
     * Name of the branch seed
     */
    private final String branchSeedName/* = "${project}-*-seed"*/;

    /**
     * Start job name for the branch
     */
    private final String branchStartName/* = "${project}-*-build"*/;

    @DataBoundConstructor
    public NamingStrategyConfig(String projectFolderPath, String branchFolderPath, String projectSeedName, String branchSeedName, String branchStartName, String commitParameter, String projectDestructorName) {
        this.projectFolderPath = projectFolderPath;
        this.branchFolderPath = branchFolderPath;
        this.projectSeedName = projectSeedName;
        this.projectDestructorName = projectDestructorName;
        this.branchSeedName = branchSeedName;
        this.branchStartName = branchStartName;
    }
}

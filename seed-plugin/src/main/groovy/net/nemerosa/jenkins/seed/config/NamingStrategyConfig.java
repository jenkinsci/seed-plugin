package net.nemerosa.jenkins.seed.config;

import lombok.Data;

@Data
public class NamingStrategyConfig {

    /**
     * Path to the project folder
     */
    private String projectFolderPath = "${project}";

    /**
     * Path to the branch folder
     */
    private String branchFolderPath = "${branch}";

    /**
     * Name of the project seed
     */
    private String projectSeedName = "${project}-seed";

    /**
     * Name of the branch seed
     */
    private String branchSeedName = "${project}-*-seed";

    /**
     * Start job name for the branch
     */
    private String branchStartName = "${project}-*-build";

    /**
     * Parameter to pass to the branch start
     */
    private String commitParameter = "COMMIT";

}

package net.nemerosa.jenkins.seed.config;

import lombok.Data;

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
     * Name of the branch seed
     */
    private final String branchSeedName/* = "${project}-*-seed"*/;

    /**
     * Start job name for the branch
     */
    private final String branchStartName/* = "${project}-*-build"*/;

    /**
     * Parameter to pass to the branch start
     */
    private final String commitParameter/* = "COMMIT"*/;

}

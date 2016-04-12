package net.nemerosa.seed.config;

/**
 * Strategy used to get the names and path of the seed jobs.
 */
public interface SeedNamingStrategy {

    /**
     * Gets the path to a project's seed, given its ID
     *
     * @param id Project's ID
     * @return Path to the project's seed
     */
    String getProjectSeed(String id);

    /**
     * Gets the path to a project's destructor, used to destroy branch folders.
     *
     * @param id Project's ID
     * @return Path to the project's destructor job
     */
    String getProjectDestructor(String id);

    /**
     * Gets the path pattern to a project's branch seed, given the project ID
     *
     * @param id Project's ID
     * @return Path pattern to a project's branch seed
     */
    String getBranchSeed(String id);

    /**
     * Gets the expanded path to a project's branch seed, given the project ID and the branch name
     *
     * @param project Project's ID
     * @param branch  Branch
     * @return Path to a project's branch seed
     */
    String getBranchSeed(String project, String branch);

    /**
     * Gets the path pattern to a project's branch start job, given the project ID
     *
     * @param id Project's ID
     * @return Path pattern to a project's branch start job
     */
    String getBranchStart(String id);

    /**
     * Gets a name useable for a branch in path pattern.
     *
     * @param branch Branch as given by the project seed job or by the connectors.
     * @return Branch path fragment
     */
    String getBranchName(String branch);
}

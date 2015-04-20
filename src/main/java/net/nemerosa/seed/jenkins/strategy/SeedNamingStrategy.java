package net.nemerosa.seed.jenkins.strategy;

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
     * Gets the path pattern to a project's branch seed, given the project ID
     *
     * @param id Project's ID
     * @return Path pattern to a project's branch seed
     */
    String getBranchSeed(String id);

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

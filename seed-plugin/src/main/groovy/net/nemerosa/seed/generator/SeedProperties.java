package net.nemerosa.seed.generator;

/**
 * Definition of the core Seed properties.
 */
public interface SeedProperties {

    /**
     * Comma-separated list of libraries to download. Each element is a Gradle notation, for example:
     * <code>my.group:artifact:2.+</code>
     */
    String SEED_DSL_LIBRARIES = "seed.dsl.libraries";

    /**
     * Name of the artifact which contains the seed script.
     */
    String SEED_DSL_SCRIPT_JAR = "seed.dsl.script.jar";

    /**
     * Location of the script within the JAR. Defaults to <code>/seed.groovy</code>
     */
    String SEED_DSL_SCRIPT_LOCATION = "seed.dsl.script.location";

    /**
     * URL of a repository to download pipeline libraries from. If not defined, the Maven Central is used by
     * default.
     */
    String SEED_DSL_REPOSITORY = "seed.dsl.repository";

}

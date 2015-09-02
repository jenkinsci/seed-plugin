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
     * default. If starting with <code>flat:</code>, it defines a directory instead of a URL and the rest of the path
     * is used as a path (can be relative to the workspace or absolute).
     */
    String SEED_DSL_REPOSITORY = "seed.dsl.repository";

    /**
     * User to use to connect to the Seed repository. Its value is evaluated using the Jenkins context
     * and can therefore use environment variables, like <code>${MY_REPOSITORY_USER}</code>, defined globally.
     */
    String SEED_DSL_REPOSITORY_USER = "seed.dsl.repository.user";

    /**
     * Password to use to connect to the Seed repository. Its value is evaluated using the Jenkins context
     * and can therefore use environment variables, like <code>${MY_REPOSITORY_PASSWORD}</code>, defined globally,
     * allowing the use of encrypted credentials.
     */
    String SEED_DSL_REPOSITORY_PASSWORD = "seed.dsl.repository.password";

}

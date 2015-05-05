package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedNamingStrategy;
import org.apache.commons.lang.StringUtils;

public final class SeedNamingStrategyHelper {

    private SeedNamingStrategyHelper() {
    }

    public static String getProjectSeedFolder(SeedNamingStrategy seedNamingStrategy, String id) {
        return getFolder(seedNamingStrategy.getProjectSeed(id));
    }

    public static String getBranchSeedFolder(SeedNamingStrategy seedNamingStrategy, String project, String branch) {
        return getFolder(seedNamingStrategy.getBranchSeed(project, branch));
    }

    public static String getBranchPath(String path, String pathBranchName) {
        return path.replace("*", pathBranchName);
    }

    private static String getFolder(String path) {
        return StringUtils.substringBeforeLast(path, "/");
    }
}

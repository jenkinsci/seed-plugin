package net.nemerosa.seed.jenkins.strategy.naming;

import net.nemerosa.seed.jenkins.strategy.SeedNamingStrategy;
import org.apache.commons.lang.StringUtils;

public final class SeedNamingStrategyHelper {

    private SeedNamingStrategyHelper() {
    }

    public static String getProjectSeedFolder(SeedNamingStrategy seedNamingStrategy, String id) {
        return getFolder(seedNamingStrategy.getProjectSeed(id));
    }

    private static String getFolder(String path) {
        return StringUtils.substringBeforeLast(path, "/");
    }
}

package net.nemerosa.seed.jenkins.model;

import org.apache.commons.lang.StringUtils;

public class SeedProjectConfiguration {

    private final String id;
    private final String name;
    private final String seed;
    private final String branchSeed;
    private final String branchStart;

    public SeedProjectConfiguration(String id, String name, String seed, String branchSeed, String branchStart) {
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.branchSeed = branchSeed;
        this.branchStart = branchStart;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeed() {
        return seed;
    }

    public String getBranchSeed() {
        return branchSeed;
    }

    public String getBranchStart() {
        return branchStart;
    }

    public static SeedProjectConfiguration of(String id) {
        return new SeedProjectConfiguration(
                id,
                defaultName(id),
                defaultSeed(id),
                defaultBranchSeed(id),
                defaultBranchStart(id)
        );
    }

    private static String defaultBranchStart(String id) {
        return String.format("%1$s/%1$s-*/%1$s-*-build", defaultName(id));
    }

    private static String defaultBranchSeed(String id) {
        return String.format("%1$s/%1$s-*/%1$s-*-seed", defaultName(id));
    }

    private static String defaultSeed(String id) {
        return String.format("%1$s/%1$s-seed", defaultName(id));
    }

    private static String defaultName(String id) {
        if (StringUtils.contains(id, "/")) {
            return normalise(StringUtils.substringAfterLast(id, "/"));
        } else {
            return normalise(id);
        }
    }

    public String getBranchSeed(String branch) {
        return branchSeed.replace("*", normaliseBranch(branch));
    }

    private static String normalise(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "-");
    }

    private String normaliseBranch(String branch) {
        return normalise(branch);
    }
}

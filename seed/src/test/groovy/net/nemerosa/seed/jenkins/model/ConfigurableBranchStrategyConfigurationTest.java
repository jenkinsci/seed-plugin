package net.nemerosa.seed.jenkins.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ConfigurableBranchStrategyConfigurationTest {

    @Test(expected = MissingParameterException.class)
    public void no_id() {
        new ConfigurableBranchStrategyConfiguration(Collections.<String, Object>emptyMap());
    }

    @Test
    public void default_id() {
        ConfigurableBranchStrategyConfiguration c = new ConfigurableBranchStrategyConfiguration(
                Collections.singletonMap("id", "test")
        );
        assertEquals("test", c.getId());
        assertEquals("${project}/${project}-seed", c.getSeedExpression());
        assertEquals("${project}/${project}-*/${project}-*-seed", c.getBranchSeedExpression());
        assertEquals("${project}/${project}-*/${project}-*-build", c.getBranchStartExpression());
        assertEquals("${branch}", c.getBranchNameExpression());
        assertEquals("COMMIT", c.getCommitParameter());
    }

    @Test
    public void custom() {
        ConfigurableBranchStrategyConfiguration c = new ConfigurableBranchStrategyConfiguration(
                ImmutableMap.<String, Object>builder()
                        .put("id", "test")
                        .put("seed-expression", "${PROJECT}/${PROJECT}_GENERATOR")
                        .put("branch-seed-expression", "${PROJECT}/${PROJECT}_*/${PROJECT}_*_GENERATOR")
                        .put("branch-start-expression", "${PROJECT}/${PROJECT}_*/${PROJECT}_*_010_BUILD")
                        .put("branch-name-expression", "${BRANCH}")
                        .put("commit-parameter", "REVISION")
                        .build()
        );
        assertEquals("test", c.getId());
        assertEquals("${PROJECT}/${PROJECT}_GENERATOR", c.getSeedExpression());
        assertEquals("${PROJECT}/${PROJECT}_*/${PROJECT}_*_GENERATOR", c.getBranchSeedExpression());
        assertEquals("${PROJECT}/${PROJECT}_*/${PROJECT}_*_010_BUILD", c.getBranchStartExpression());
        assertEquals("${BRANCH}", c.getBranchNameExpression());
        assertEquals("REVISION", c.getCommitParameter());
    }

    @Test
    public void custom_branch_name_prefixes() {
        ConfigurableBranchStrategyConfiguration c = new ConfigurableBranchStrategyConfiguration(
                ImmutableMap.<String, Object>builder()
                        .put("id", "test")
                        .put("branch-name-prefixes", Arrays.asList(
                                "branches/",
                                "tags/"
                        ))
                        .build()
        );
        assertEquals("test", c.getId());
        assertEquals("${project}/${project}-seed", c.getSeedExpression());
        assertEquals("${project}/${project}-*/${project}-*-seed", c.getBranchSeedExpression());
        assertEquals("${project}/${project}-*/${project}-*-build", c.getBranchStartExpression());
        assertEquals("${branch}", c.getBranchNameExpression());
        assertEquals(
                Arrays.asList(
                        "branches/",
                        "tags/"
                ),
                c.getBranchNamePrefixes()
        );
        assertEquals("COMMIT", c.getCommitParameter());
    }

}

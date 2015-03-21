package net.nemerosa.seed.jenkins.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SeedProjectConfigurationTest {

    @Test
    public void defaults() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of("nemerosa/ontrack");
        assertEquals("nemerosa/ontrack", c.getId());
        assertEquals("ontrack", c.getName());
        assertEquals("ontrack/ontrack-seed", c.getSeed());
        assertEquals("ontrack/ontrack-*/ontrack-*-seed", c.getBranchSeed());
        assertEquals("ontrack/ontrack-*/ontrack-*-build", c.getBranchStart());
    }

    @Test
    public void defaults_for_simple_project() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of("ontrack");
        assertEquals("ontrack", c.getId());
        assertEquals("ontrack", c.getName());
        assertEquals("ontrack/ontrack-seed", c.getSeed());
        assertEquals("ontrack/ontrack-*/ontrack-*-seed", c.getBranchSeed());
        assertEquals("ontrack/ontrack-*/ontrack-*-build", c.getBranchStart());
    }

}

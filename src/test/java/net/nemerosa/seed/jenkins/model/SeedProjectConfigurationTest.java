package net.nemerosa.seed.jenkins.model;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SeedProjectConfigurationTest {

    @Test
    public void defaults() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of(Collections.singletonMap("id", "nemerosa/ontrack"));
        assertEquals("nemerosa/ontrack", c.getId());
        assertEquals("ontrack", c.getName());
    }

    @Test
    public void defaults_for_simple_project() {
        SeedProjectConfiguration c = SeedProjectConfiguration.of("ontrack");
        assertEquals("ontrack", c.getId());
        assertEquals("ontrack", c.getName());
    }

}

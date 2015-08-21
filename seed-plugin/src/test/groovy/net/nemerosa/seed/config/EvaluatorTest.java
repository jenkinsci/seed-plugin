package net.nemerosa.seed.config;

import org.junit.Assert;
import org.junit.Test;

public class EvaluatorTest {

    @Test
    public void lower() {
        Assert.assertEquals("test", Evaluator.evaluate("${project}", "project", "test"));
    }

    @Test
    public void upper() {
        Assert.assertEquals("TEST", Evaluator.evaluate("${PROJECT}", "project", "test"));
    }

    @Test
    public void upper_with_dash() {
        Assert.assertEquals("RELEASE-1.0", Evaluator.evaluate("${BRANCH}", "branch", "release-1.0"));
    }

    @Test
    public void upper_underscore_with_dash() {
        Assert.assertEquals("RELEASE_1.0", Evaluator.evaluate("${BRANCH_}", "branch", "release-1.0"));
    }

    @Test
    public void upper_underscore_with_several_dashes() {
        Assert.assertEquals("RELEASE_1.0_RC", Evaluator.evaluate("${BRANCH_}", "branch", "release-1.0-rc"));
    }

    @Test
    public void capitalize() {
        Assert.assertEquals("Test", Evaluator.evaluate("${Project}", "project", "test"));
    }

    @Test
    public void unknown() {
        Assert.assertEquals("${proj}", Evaluator.evaluate("${proj}", "project", "test"));
    }

    @Test
    public void full() {
        Assert.assertEquals("TEST/TEST_*/TEST_*_GENERATOR", Evaluator.evaluate("${PROJECT}/${PROJECT}_*/${PROJECT}_*_GENERATOR", "project", "test"));
    }

}

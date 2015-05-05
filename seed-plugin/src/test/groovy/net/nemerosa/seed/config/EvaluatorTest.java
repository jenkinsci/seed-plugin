package net.nemerosa.seed.config;

import net.nemerosa.seed.config.Evaluator;
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

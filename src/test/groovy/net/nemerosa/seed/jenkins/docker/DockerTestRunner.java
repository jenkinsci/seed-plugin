package net.nemerosa.seed.jenkins.docker;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class DockerTestRunner extends BlockJUnit4ClassRunner {

    private final boolean enabled;

    public DockerTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.enabled = StringUtils.isNotBlank(System.getProperty("jenkinsUrl"));
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (enabled) {
            super.runChild(method, notifier);
        } else {
            notifier.fireTestIgnored(describeChild(method));
        }
    }
}

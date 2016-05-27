package net.nemerosa.jenkins.seed.test;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

@Deprecated
public class AcceptanceTestRunner extends BlockJUnit4ClassRunner {

    private final boolean enabled;

    public AcceptanceTestRunner(Class<?> klass) throws InitializationError {
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

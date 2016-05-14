package net.nemerosa.jenkins.seed.generator.scm;

import com.google.inject.AbstractModule;

public class SCMServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SCMServiceRegistry.class).to(SCMServiceRegistryImpl.class);
    }
}

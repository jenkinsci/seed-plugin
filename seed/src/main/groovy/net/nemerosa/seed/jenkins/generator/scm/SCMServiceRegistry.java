package net.nemerosa.seed.jenkins.generator.scm;

public interface SCMServiceRegistry {

    SCMService getScm(String id);

}

package net.nemerosa.jenkins.seed.generator.scm;

public interface SCMServiceRegistry {

    SCMService getScm(String id);

}

package net.nemerosa.jenkins.seed.generator.scm;

public interface SCMService {

    String getId();

    String generatePartial(String scmUrl, String scmCredentials, String branch, String path);
}

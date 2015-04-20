package net.nemerosa.seed.jenkins.scm

interface SCMService {

    void downloadPartial(def scm, String scmUrl, String scmBranch, String path)

}
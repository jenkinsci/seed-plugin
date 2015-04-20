package net.nemerosa.seed.jenkins.scm

class SCMHelper {

    static void downloadPartial(def scm, String scmType, String scmUrl, String scmBranch, String path) {
        // Gets the SCM
        SCMService service = SCMRegister.get(scmType)
        // Applies the SCM
        service.downloadPartial(scm, scmUrl, scmBranch, path)
    }

}

package net.nemerosa.jenkins.seed.integration

import org.apache.commons.io.FileUtils

class IntegrationSupport {

    static File createTempDir(String id) {
        File file = File.createTempFile("seed-test-${id}", '.d')
        FileUtils.forceDelete(file)
        file.mkdirs()
        file.deleteOnExit()
        return file
    }

}

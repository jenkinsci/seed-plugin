package net.nemerosa.jenkins.seed.integration.git

import net.nemerosa.jenkins.seed.integration.IntegrationSupport
import org.eclipse.jgit.api.Git

class GitRepo {

    /**
     * Prepares a Git repository in a directory, initialise it with some content identified by the {@code id} name
     * and returns the path of the directory.
     */
    static String prepare(String id, String branch = 'master') {
        Map<String, String> resources = [:]
        loadResource(resources, "seed-${id}.properties", "seed/seed.properties")
        loadResource(resources, "seed-${id}.groovy", "seed/seed.groovy")
        return prepare(id, resources, branch)
    }

    static def loadResource(Map<String, String> resources, String name, String targetName) {
        def url = GitRepo.class.getResource("/acceptance/${name}")
        if (url) {
            resources.put targetName, url.text
        }
    }

    /**
     * Prepares a Git repository in a directory, initialise it with some content identified by the {@code id} name
     * and returns the path of the directory.
     */
    static String prepare(String id, Map<String, String> resources, String branch = 'master') {
        File dir = IntegrationSupport.createTempDir(id)
        println "Creating Git directory for ${id} at ${dir}..."

        // Initialisation
        Git git = Git.init().setDirectory(dir).call()

        // Adding the resources
        resources.each { name, content ->
            def file = new File(dir, name)
            file.parentFile.mkdirs()
            file.text = content
            git.add().addFilepattern(name).call()
        }

        // Committing
        git.commit().setMessage("Seed files").setAuthor("Nemerosa", "nemerosa@nemerosa.net").call()

        // Branch?
        if (branch != 'master') {
            git.checkout().setName(branch).setCreateBranch(true).call()
        }

        // OK
        return dir.absolutePath
    }
}

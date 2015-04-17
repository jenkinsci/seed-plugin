package net.nemerosa.seed.jenkins.step;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.Cause;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.WithPlugin;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// FIXME Waiting for JENKINS-27994 to be fixed and a new job-dsl released
@Ignore
public class ProjectSeedBuilderTest {
    /**
     * The Jenkins Rule.
     */
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @WithPlugin("cloudbees-folder.hpi")
    public void seed_generation() throws Exception {
        FreeStyleProject project = j.getInstance().createProject(FreeStyleProject.class, "seed");
        project.getBuildersList().add(new ProjectSeedBuilder(
                "my_project",
                "test",
                "/seed/sample"
        ));
        Future<FreeStyleBuild> future = project.scheduleBuild2(0, new Cause.UserIdCause());
        FreeStyleBuild build = future.get(10, TimeUnit.SECONDS);
        j.assertBuildStatus(Result.SUCCESS, build);

        // Gets the created folder
        Folder folder = (Folder) j.getInstance().getItem("my_project");
        assertNotNull(folder);
        assertEquals("my_project", folder.getName());

        // Gets the created project seed
        FreeStyleProject projectSeed = (FreeStyleProject) folder.getItem("my_project-seed");
        assertNotNull(projectSeed);
    }

}


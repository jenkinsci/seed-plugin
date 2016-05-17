package net.nemerosa.jenkins.seed.generator;

import com.google.inject.Guice;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.seed.triggering.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Configuration of a Project job when it's time to generate/update a branch.
 */
public class BranchDestructionStep extends Builder {

    private final String project;
    private final String branch;

    @DataBoundConstructor
    public BranchDestructionStep(String project, String branch) {
        this.project = project;
        this.branch = branch;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        SeedService seedService = Guice.createInjector(new SeedServiceModule()).getInstance(SeedService.class);

        // Environment for the DSL execution
        EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        // Gets actual properties
        String theProject = env.expand(project);
        String theBranch = env.expand(branch);

        // Posts an event for the destruction of the branch
        seedService.post(
                new SeedEvent(
                        theProject,
                        theBranch,
                        SeedEventType.DELETION,
                        SeedChannel.of("destructor", "Destructor job")
                )
        );

        // OK
        return true;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    @Extension
    public static class BranchDestructionStepExtension extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch folder deletion";
        }
    }
}

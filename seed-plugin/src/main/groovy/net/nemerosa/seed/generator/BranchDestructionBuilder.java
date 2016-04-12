package net.nemerosa.seed.generator;

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
 * Build step which creates a branch folder and a branch seed inside.
 */
public class BranchDestructionBuilder extends Builder {

    private final String project;
    private final String branch;

    protected final SeedService seedService;

    @DataBoundConstructor
    public BranchDestructionBuilder(String project, String branch) {
        this.project = project;
        this.branch = branch;
        this.seedService = Guice.createInjector(new SeedServiceModule()).getInstance(SeedService.class);
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

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

    @Extension
    public static class BranchSeedBuilderDescription extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch folder destructor";
        }
    }
}

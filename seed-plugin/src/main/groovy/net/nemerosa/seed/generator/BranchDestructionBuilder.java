package net.nemerosa.seed.generator;

import com.google.inject.Guice;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.generator.BranchDestructionStep;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import net.nemerosa.seed.triggering.SeedServiceV0Module;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build step which creates a branch folder and a branch seed inside.
 */
@Deprecated
public class BranchDestructionBuilder extends BranchDestructionStep {

    @DataBoundConstructor
    public BranchDestructionBuilder(String project, String branch) {
        super(project, branch);
    }

    @Override
    protected SeedService getSeedService() {
        return Guice.createInjector(new SeedServiceV0Module()).getInstance(SeedService.class);
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

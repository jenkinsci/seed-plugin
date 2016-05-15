package net.nemerosa.jenkins.seed.generator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import hudson.EnvVars;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import net.nemerosa.jenkins.seed.generator.scm.SCMService;
import net.nemerosa.jenkins.seed.generator.scm.SCMServiceModule;
import net.nemerosa.jenkins.seed.generator.scm.SCMServiceRegistry;
import org.apache.commons.lang.StringUtils;

public class BranchSeedSCMExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;
    private final String branchParameter;
    private final EnvVars env;

    private final Injector injector = Guice.createInjector(new SCMServiceModule());

    public BranchSeedSCMExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters, String branchParameter, EnvVars env) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
        this.branchParameter = branchParameter;
        this.env = env;
    }

    @Override
    public String generate() {
        // Gets the SCM registry
        SCMServiceRegistry scmServiceRegistry = injector.getInstance(SCMServiceRegistry.class);
        // Gets the SCM service
        SCMService scmService = scmServiceRegistry.getScm(parameters.getScmType());
        // SCM branch?
        String scmBranch = branchParameter;
        if (projectConfig.getPipelineConfig().isBranchSCMParameter()) {
            scmBranch = env.get("BRANCH_SCM", "");
            if (StringUtils.isBlank(scmBranch)) {
                throw new IllegalStateException("Branch SCM parameter option is active but no BRANCH_SCM environment variable was found.");
            }
        }
        // Generation
        return scmService.generatePartial(parameters.getScmUrl(), parameters.getScmCredentials(), scmBranch, "seed");
    }
}

package net.nemerosa.jenkins.seed.generator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig;
import net.nemerosa.jenkins.seed.generator.scm.SCMService;
import net.nemerosa.jenkins.seed.generator.scm.SCMServiceModule;
import net.nemerosa.jenkins.seed.generator.scm.SCMServiceRegistry;

public class BranchSeedSCMExtension implements GenerationExtension {

    private final ProjectPipelineConfig projectConfig;
    private final ProjectParameters parameters;
    private final String branchParameter;

    private final Injector injector = Guice.createInjector(new SCMServiceModule());

    public BranchSeedSCMExtension(ProjectPipelineConfig projectConfig, ProjectParameters parameters, String branchParameter) {
        this.projectConfig = projectConfig;
        this.parameters = parameters;
        this.branchParameter = branchParameter;
    }

    @Override
    public String generate() {
        // Gets the SCM registry
        SCMServiceRegistry scmServiceRegistry = injector.getInstance(SCMServiceRegistry.class);
        // Gets the SCM service
        SCMService scmService = scmServiceRegistry.getScm(parameters.getScmType());
        // Generation
        return scmService.generatePartial(parameters.getScmUrl(), parameters.getScmCredentials(), branchParameter, "seed");
    }
}

package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class PipelineConfig {

    /**
     * Boolean to enable the creation of a destructor job.
     */
    private final boolean destructor;

    /**
     * Parameter to pass to the branch start
     */
    private final String commitParameter;

    /**
     * Line separated list of authorisations for a project
     */
    private final String authorisations;

    /**
     * Naming strategy
     */
    private final NamingStrategyConfig namingStrategy;

    /**
     * Events configurations
     */
    private final EventStrategyConfig eventStrategy;

    @DataBoundConstructor
    public PipelineConfig(boolean destructor, String commitParameter, String authorisations, NamingStrategyConfig namingStrategy, EventStrategyConfig eventStrategy) {
        this.destructor = destructor;
        this.commitParameter = commitParameter;
        this.authorisations = authorisations;
        this.namingStrategy = namingStrategy;
        this.eventStrategy = eventStrategy;
    }

    public String getProjectFolder(ProjectParameters parameters) {
        return namingStrategy.getProjectFolder(parameters);
    }

    public String getProjectSeedJob(ProjectParameters parameters) {
        return namingStrategy.getProjectSeedJob(parameters);
    }

    public String getProjectDestructorJob(ProjectParameters parameters) {
        return namingStrategy.getProjectDestructorJob(parameters);
    }

    public List<String> getProjectAuthorisations(ProjectParameters parameters) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(authorisations)) {
            String[] array = StringUtils.split(authorisations, "\n");
            for (String line : array) {
                line = StringUtils.trim(line);
                if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
                    String token = StringUtils.replace(line, "*", parameters.getProject());
                    list.add(token);
                }
            }
        }
        return list;
    }
}

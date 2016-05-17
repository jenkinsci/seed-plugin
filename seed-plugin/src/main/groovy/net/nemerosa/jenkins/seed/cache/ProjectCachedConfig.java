package net.nemerosa.jenkins.seed.cache;

import lombok.Data;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import net.nemerosa.jenkins.seed.config.ProjectParameters;
import net.nemerosa.jenkins.seed.triggering.SeedChannel;

import static java.lang.String.format;

@Data
public class ProjectCachedConfig {

    private final PipelineConfig pipelineConfig;
    private final ProjectParameters projectParameters;

    public ProjectCachedConfig(String project) {
        this.pipelineConfig = new PipelineConfig();
        this.projectParameters = new ProjectParameters(
                project,
                "",
                "",
                ""
        );
    }

    public ProjectCachedConfig(PipelineConfig pipelineConfig, ProjectParameters projectParameters) {
        this.pipelineConfig = pipelineConfig;
        this.projectParameters = projectParameters;
    }

    public boolean isChannelEnabled(SeedChannel channel) {
        // FIXME Method net.nemerosa.jenkins.seed.cache.ProjectCachedConfig.isChannelEnabled
        return true;
    }

    public String getSecretKey(String context) {
        // FIXME Method net.nemerosa.jenkins.seed.cache.ProjectCachedConfig.getSecretKey
        return null;
    }

    public String getProjectSeedJob() {
        return format(
                "%s/%s",
                pipelineConfig.getProjectFolder(projectParameters),
                pipelineConfig.getProjectSeedJob(projectParameters)
        );
    }

    public String getBranchSeedJob(String branch) {
        return format(
                "%s/%s/%s",
                pipelineConfig.getProjectFolder(projectParameters),
                pipelineConfig.getBranchFolderPath(projectParameters, branch),
                pipelineConfig.getBranchSeedName(projectParameters, branch)
        );
    }

    public boolean isDelete() {
        return pipelineConfig.getEventStrategy().isDelete();
    }

    public boolean isAuto() {
        return pipelineConfig.getEventStrategy().isAuto();
    }

    public boolean isTrigger() {
        return pipelineConfig.getEventStrategy().isTrigger();
    }

    public String getBranchStartJob(String branch) {
        return format(
                "%s/%s/%s",
                pipelineConfig.getProjectFolder(projectParameters),
                pipelineConfig.getBranchFolderPath(projectParameters, branch),
                pipelineConfig.getBranchStartName(projectParameters, branch)
        );
    }

    public String getCommitParameter() {
        return pipelineConfig.getCommitParameter();
    }
}

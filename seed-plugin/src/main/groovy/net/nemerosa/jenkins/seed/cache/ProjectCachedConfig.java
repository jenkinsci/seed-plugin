package net.nemerosa.jenkins.seed.cache;

import lombok.Data;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import net.nemerosa.jenkins.seed.triggering.SeedChannel;

import static java.lang.String.format;

@Data
public class ProjectCachedConfig {

    private final String project;
    private final PipelineConfig pipelineConfig;

    public ProjectCachedConfig(String project) {
        this.project = project;
        this.pipelineConfig = new PipelineConfig();
    }

    public ProjectCachedConfig(String project, PipelineConfig pipelineConfig) {
        this.project = project;
        this.pipelineConfig = pipelineConfig;
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
                pipelineConfig.getProjectFolder(project),
                pipelineConfig.getProjectSeedJob(project)
        );
    }

    public String getBranchSeedJob(String branch) {
        return format(
                "%s/%s/%s",
                pipelineConfig.getProjectFolder(project),
                pipelineConfig.getBranchFolderPath(project, branch),
                pipelineConfig.getBranchSeedName(project, branch)
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
                pipelineConfig.getProjectFolder(project),
                pipelineConfig.getBranchFolderPath(project, branch),
                pipelineConfig.getBranchStartName(project, branch)
        );
    }

    public String getCommitParameter() {
        return pipelineConfig.getCommitParameter();
    }
}

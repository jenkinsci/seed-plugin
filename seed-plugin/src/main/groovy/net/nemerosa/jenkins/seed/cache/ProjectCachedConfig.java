package net.nemerosa.jenkins.seed.cache;

import lombok.Data;
import net.nemerosa.jenkins.seed.config.PipelineConfig;
import net.nemerosa.jenkins.seed.config.ProjectSeed;
import net.nemerosa.jenkins.seed.triggering.SeedChannel;
import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;

@Data
public class ProjectCachedConfig {

    private final ProjectSeed seed;
    private final PipelineConfig pipelineConfig;

    public ProjectCachedConfig(String project) {
        this.seed = new ProjectSeed(project, "", "");
        this.pipelineConfig = new PipelineConfig();
    }

    public ProjectCachedConfig(ProjectSeed seed, PipelineConfig pipelineConfig) {
        this.seed = seed;
        this.pipelineConfig = pipelineConfig;
    }

    public boolean isChannelEnabled(SeedChannel channel) {
        String type = seed.getTriggerType();
        return StringUtils.isNotBlank(type) && StringUtils.equals(channel.getId(), type);
    }

    public String getSecretKey() {
        return seed.getTriggerSecret();
    }

    public String getProjectSeedJob() {
        return format(
                "%s/%s",
                pipelineConfig.getProjectFolder(seed.getProject()),
                pipelineConfig.getProjectSeedJob(seed.getProject())
        );
    }

    public String getBranchSeedJob(String branch) {
        return format(
                "%s/%s/%s",
                pipelineConfig.getProjectFolder(seed.getProject()),
                pipelineConfig.getBranchFolderPath(seed.getProject(), branch),
                pipelineConfig.getBranchSeedName(seed.getProject(), branch)
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
                pipelineConfig.getProjectFolder(seed.getProject()),
                pipelineConfig.getBranchFolderPath(seed.getProject(), branch),
                pipelineConfig.getBranchStartName(seed.getProject(), branch)
        );
    }

    public String getCommitParameter() {
        return pipelineConfig.getCommitParameter();
    }
}

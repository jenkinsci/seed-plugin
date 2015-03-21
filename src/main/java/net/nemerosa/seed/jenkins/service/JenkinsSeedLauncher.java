package net.nemerosa.seed.jenkins.service;

import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.SeedLauncher;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class JenkinsSeedLauncher implements SeedLauncher {

    @Override
    public void launch(String path, Map<String, String> parameters) {
        // Gets the job using its path
        Job job = findJob(Jenkins.getInstance(), "", path);
        // FIXME Launches the job
    }

    private Job findJob(ItemGroup<?> container, String context, String path) {
        if (StringUtils.contains(path, "/")) {
            String prefix = StringUtils.substringBefore(path, "/");
            String rest = StringUtils.substringAfter(path, "/");
            Item item = container.getItem(prefix);
            if (item instanceof ItemGroup) {
                return findJob((ItemGroup) item, context + "/" + path, rest);
            } else {
                throw new CannotFindJobException(context, path);
            }
        } else {
            Item item = container.getItem(path);
            if (item instanceof Job) {
                return (Job) item;
            } else {
                throw new CannotFindJobException(context, path);
            }
        }
    }

}

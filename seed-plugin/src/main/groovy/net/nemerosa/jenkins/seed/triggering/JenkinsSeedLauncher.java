package net.nemerosa.jenkins.seed.triggering;

import hudson.model.*;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import net.nemerosa.seed.config.CannotDeleteItemException;
import net.nemerosa.seed.config.CannotFindJobException;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class JenkinsSeedLauncher implements SeedLauncher {

    private static final Logger LOGGER = Logger.getLogger(JenkinsSeedLauncher.class.getName());

    @Override
    public void launch(SeedChannel channel, String path, Map<String, String> parameters) {
        LOGGER.info(String.format("Launching job at %s with parameters %s", path, parameters));
        SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
        try {
            // Gets the job using its path
            final AbstractProject job = findJob(path);
            // Launches the job
            if (parameters != null && !parameters.isEmpty()) {
                if (job.isParameterized()) {
                    // List of parameters
                    List<ParameterValue> parameterValues = new ArrayList<>();
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        parameterValues.add(
                                new StringParameterValue(
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        );
                    }
                    // Scheduling
                    Jenkins.getInstance().getQueue()
                            .schedule2(
                                    job,
                                    0,
                                    new ParametersAction(parameterValues),
                                    new CauseAction(getCause(channel))
                            );
                } else {
                    Jenkins.getInstance().getQueue().schedule2(
                            job,
                            0,
                            new CauseAction(getCause(channel))
                    );
                }
            } else {
                Jenkins.getInstance().getQueue().schedule2(
                        job,
                        0,
                        new CauseAction(getCause(channel))
                );
            }
        } finally {
            SecurityContextHolder.setContext(orig);
        }
    }

    @Override
    public void delete(String path) {
        LOGGER.info(String.format("Deleting item at %s", path));
        try {
            SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
            try {
                Item root = findItem(path);
                // Deletes all children
                for (Job job : root.getAllJobs()) {
                    LOGGER.info(String.format("\tDeleting item at %s", job.getName()));
                    job.delete();
                }
                // Deletes the root
                LOGGER.info(String.format("\tDeleting item at %s", root.getName()));
                root.delete();
            } finally {
                SecurityContextHolder.setContext(orig);
            }
        } catch (IOException | InterruptedException e) {
            throw new CannotDeleteItemException(path, e);
        }
    }

    private Cause getCause(final SeedChannel channel) {
        return new SeedCause(channel);
    }

    private AbstractProject findJob(String path) {
        Item item = findItem(path);
        if (item instanceof AbstractProject) {
            return (AbstractProject) item;
        } else {
            throw new CannotFindJobException("", path);
        }
    }

    private Item findItem(String path) {
        return findItem(Jenkins.getInstance(), "", path);
    }

    private Item findItem(ItemGroup<?> container, String context, String path) {
        if (StringUtils.contains(path, "/")) {
            String prefix = StringUtils.substringBefore(path, "/");
            String rest = StringUtils.substringAfter(path, "/");
            Item item = container.getItem(prefix);
            if (item instanceof ItemGroup) {
                return findItem((ItemGroup) item, context + "/" + prefix, rest);
            } else {
                throw new CannotFindJobException(context, path);
            }
        } else {
            Item item = container.getItem(path);
            if (item != null) {
                return item;
            } else {
                throw new CannotFindJobException(context, path);
            }
        }
    }

}

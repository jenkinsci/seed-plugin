package net.nemerosa.seed.triggering;

import hudson.model.*;
import jenkins.model.Jenkins;
import net.nemerosa.seed.config.CannotDeleteItemException;
import net.nemerosa.seed.config.CannotFindJobException;
import net.nemerosa.seed.config.JobNotParameterizedException;
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
        // Gets the job using its path
        final AbstractProject job = findJob(path);
        // Launches the job
        if (parameters != null && !parameters.isEmpty()) {
            if (job.isParameterized()) {
                // List of parameters
                List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
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
                throw new JobNotParameterizedException(job.getName());
            }
        } else {
            job.scheduleBuild(getCause(channel));
        }
    }

    @Override
    public void delete(String path) {
        Item item = findItem(path);
        try {
            item.delete();
        } catch (IOException e) {
            throw new CannotDeleteItemException(path, e);
        } catch (InterruptedException e) {
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

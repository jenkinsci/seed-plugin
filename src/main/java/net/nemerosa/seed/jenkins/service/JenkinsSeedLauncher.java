package net.nemerosa.seed.jenkins.service;

import hudson.model.*;
import jenkins.model.Jenkins;
import net.nemerosa.seed.jenkins.SeedLauncher;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JenkinsSeedLauncher implements SeedLauncher {

    @Override
    public void launch(String path, Map<String, String> parameters) {
        // Gets the job using its path
        final AbstractProject job = findJob(Jenkins.getInstance(), "", path);
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
                                new CauseAction(getCause())
                        );
            } else {
                throw new JobNotParameterizedException(job.getName());
            }
        } else {
            job.scheduleBuild(getCause());
        }
    }

    private Cause getCause() {
        return new Cause() {
            @Override
            public String getShortDescription() {
                // FIXME Use the end point as a source of information
                return "Seed plug-in";
            }
        };
    }

    private AbstractProject findJob(ItemGroup<?> container, String context, String path) {
        if (StringUtils.contains(path, "/")) {
            String prefix = StringUtils.substringBefore(path, "/");
            String rest = StringUtils.substringAfter(path, "/");
            Item item = container.getItem(prefix);
            if (item instanceof ItemGroup) {
                return findJob((ItemGroup) item, context + "/" + prefix, rest);
            } else {
                throw new CannotFindJobException(context, path);
            }
        } else {
            Item item = container.getItem(path);
            if (item instanceof AbstractProject) {
                return (AbstractProject) item;
            } else {
                throw new CannotFindJobException(context, path);
            }
        }
    }

}

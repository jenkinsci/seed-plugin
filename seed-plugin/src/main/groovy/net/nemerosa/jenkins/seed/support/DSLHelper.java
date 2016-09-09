package net.nemerosa.jenkins.seed.support;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedView;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.plugin.LookupStrategy;
import jenkins.model.Jenkins;

import java.io.IOException;

public class DSLHelper {

    public static void launchGenerationScript(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env, String script) throws IOException {

        // Jobs are created at the Jenkins root level
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger(), env, build, build.getWorkspace(), LookupStrategy.SEED_JOB);

        // Generation
        DslScriptLoader scriptLoader = new DslScriptLoader(jm);
        GeneratedItems generatedItems = scriptLoader.runScript(script);

        // Logging
        for (GeneratedJob job : generatedItems.getJobs()) {
            listener.getLogger().format("Generated item: %s", job);
        }
        for (GeneratedView view : generatedItems.getViews()) {
            listener.getLogger().format("Generated view: %s", view);
        }

        // Done
        Jenkins.getInstance().rebuildDependencyGraph();
    }
}

package net.nemerosa.jenkins.seed.support;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import javaposse.jobdsl.dsl.*;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.plugin.LookupStrategy;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.net.URL;

public class DSLHelper {

    public static void launchGenerationScript(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env, String script) throws IOException {

        // Jobs are created at the Jenkins root level
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger(), env, build, LookupStrategy.JENKINS_ROOT);

        // Generation request
        ScriptRequest scriptRequest = new ScriptRequest(
                null,
                script,
                new URL[0],
                false // not ignoring existing,
        );

        // Generation
        GeneratedItems generatedItems = DslScriptLoader.runDslEngine(
                scriptRequest,
                jm
        );

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

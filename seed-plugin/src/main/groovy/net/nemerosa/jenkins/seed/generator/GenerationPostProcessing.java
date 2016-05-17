package net.nemerosa.jenkins.seed.generator;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

public interface GenerationPostProcessing {

    void run(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env);

}

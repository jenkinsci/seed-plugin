package net.nemerosa.jenkins.seed.generator;

import com.google.common.base.Function;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import net.nemerosa.jenkins.seed.support.DSLHelper;
import net.nemerosa.seed.config.SeedDSLHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractGenerationStep extends Builder {

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        // Default environment for the DSL execution
        final EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        // Function to expand the values
        Function<String, String> expandFn = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return env.expand(input);
            }
        };

        // Creates the environment & configures the script
        GenerationContext context = configure(expandFn, env);

        // Generation script
        String scriptPath = getScriptPath();
        listener.getLogger().format("Script: %s%n", scriptPath);
        String script = IOUtils.toString(SeedDSLHelper.class.getResource(scriptPath));

        // Traces
        for (Map.Entry<String, String> entry : context.getEnvironment().entrySet()) {
            listener.getLogger().format("Config: %s: %s%n", entry.getKey(), entry.getValue());
        }
        env.putAll(context.getEnvironment());

        // Replacements
        script = replaceExtensionPoints(listener, script, context.getExtensions());

        // Saves the script
        build.getWorkspace().child("dsl.groovy").write(script, "UTF-8");

        // Runs the script
        DSLHelper.launchGenerationScript(build, listener, env, script);

        // OK
        return true;
    }

    protected abstract GenerationContext configure(Function<String, String> expandFn, EnvVars env);

    protected abstract String getScriptPath();

    private String replaceExtensionPoints(BuildListener listener, String script, Map<String, GenerationExtension> extensions) {
        AtomicReference<String> result = new AtomicReference<>(script);
        for (Map.Entry<String, GenerationExtension> entry : extensions.entrySet()) {
            String extensionKey = entry.getKey();
            String oldResult = result.get();
            String extensionScript = entry.getValue().generate();
            // Logging
            listener.getLogger().format("Extension %s has produced:%n%s%n", extensionKey, extensionScript);
            // Replacement
            result.set(
                    replaceExtensionPoint(
                            result.get(),
                            extensionKey,
                            extensionScript
                    )
            );
            if (StringUtils.equals(oldResult, result.get())) {
                throw new IllegalStateException(String.format("Extension %s has NOT been applied.%n", extensionKey));
            } else {
                listener.getLogger().format("Extension %s has been applied.%n", extensionKey);
            }
        }
        // OK
        return result.get();
    }

    private String replaceExtensionPoint(String script, String extensionPoint, String extension) {
        return script.replace(
                String.format("%sExtensionPoint()", extensionPoint),
                extension
        );
    }

}

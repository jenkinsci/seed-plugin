package net.nemerosa.seed.jenkins.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.Map;

public class SeedConfiguration extends Configuration {

    private final Map<String, SeedProjectConfiguration> projects;
    private final Map<String, ConfigurableBranchStrategyConfiguration> configurableBranchStrategyConfigurations;

    public SeedConfiguration(Map<String, ?> data) {
        super(data);
        this.projects = Maps.uniqueIndex(
                Lists.transform(
                        getList("projects"),
                        new Function<Map<String, ?>, SeedProjectConfiguration>() {
                            @Override
                            public SeedProjectConfiguration apply(Map<String, ?> input) {
                                return SeedProjectConfiguration.of(input);
                            }
                        }
                ),
                new Function<SeedProjectConfiguration, String>() {
                    @Override
                    public String apply(SeedProjectConfiguration input) {
                        return input.getId();
                    }
                }
        );
        this.configurableBranchStrategyConfigurations = Maps.uniqueIndex(
                Lists.transform(
                        getList("strategies"),
                        new Function<Map<String, ?>, ConfigurableBranchStrategyConfiguration>() {
                            @Override
                            public ConfigurableBranchStrategyConfiguration apply(Map<String, ?> input) {
                                return ConfigurableBranchStrategyConfiguration.of(input);
                            }
                        }
                ),
                new Function<ConfigurableBranchStrategyConfiguration, String>() {
                    @Override
                    public String apply(ConfigurableBranchStrategyConfiguration input) {
                        return input.getId();
                    }
                }
        );
    }

    public boolean isAutoConfigure() {
        return getBoolean("auto-configure", false, true);
    }

    public SeedProjectConfiguration getProjectConfiguration(String id) {
        SeedProjectConfiguration configuration = projects.get(id);
        if (configuration != null) {
            return configuration;
        } else if (isAutoConfigure()) {
            return SeedProjectConfiguration.of(id);
        } else {
            throw new ProjectNotConfiguredException(id);
        }
    }

    public Map<String, ConfigurableBranchStrategyConfiguration> getConfigurableBranchStrategyConfigurations() {
        return configurableBranchStrategyConfigurations;
    }

    public static SeedConfiguration parseYaml(String text) {
        if (StringUtils.isBlank(text)) {
            return new SeedConfiguration(Collections.<String, Object>emptyMap());
        } else {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, ?> result = (Map<String, ?>) yaml.load(text);
            // Parsing
            return new SeedConfiguration(result);
        }
    }

}

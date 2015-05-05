package net.nemerosa.seed.triggering;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum SeedEventType {

    CREATION(),

    DELETION(),

    SEED(),

    COMMIT("commit");

    private final List<String> parameterNames;

    SeedEventType(String... parameterNames) {
        this.parameterNames = ImmutableList.copyOf(parameterNames);
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

}

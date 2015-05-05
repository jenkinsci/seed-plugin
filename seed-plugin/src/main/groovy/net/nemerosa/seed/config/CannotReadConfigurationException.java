package net.nemerosa.seed.config;

import net.nemerosa.seed.config.SeedException;

import java.io.IOException;

public class CannotReadConfigurationException extends SeedException {
    public CannotReadConfigurationException(String url, IOException ex) {
        super(ex, "Cannot read configuration at %s", url);
    }
}

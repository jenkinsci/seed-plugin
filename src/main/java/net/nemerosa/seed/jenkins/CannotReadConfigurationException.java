package net.nemerosa.seed.jenkins;

import net.nemerosa.seed.jenkins.model.SeedException;

import java.io.IOException;

public class CannotReadConfigurationException extends SeedException {
    public CannotReadConfigurationException(String url, IOException ex) {
        super(ex, "Cannot read configuration at %s", url);
    }
}

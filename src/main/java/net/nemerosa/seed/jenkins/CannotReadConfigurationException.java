package net.nemerosa.seed.jenkins;

import java.io.IOException;

public class CannotReadConfigurationException extends RuntimeException {
    public CannotReadConfigurationException(String url, IOException ex) {
        super(String.format("Cannot read configuration at %s", url), ex);
    }
}

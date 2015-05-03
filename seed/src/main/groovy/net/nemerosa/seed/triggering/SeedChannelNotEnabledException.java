package net.nemerosa.seed.triggering;

import net.nemerosa.seed.config.SeedException;

public class SeedChannelNotEnabledException extends SeedException {
    public SeedChannelNotEnabledException(String key, String channelId, String project) {
        super(
                "The %s channel is not enabled to trigger event for the % project, because " +
                        "the %s key has been set to `no`.",
                channelId,
                project,
                key
        );
    }
}

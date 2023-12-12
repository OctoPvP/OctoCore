package net.octopvp.octocore.common.interfaces;

import java.util.UUID;

public interface IPlayerBase {
    default UUID getUuid() {
        return getUniqueId();
    }

    UUID getUniqueId();

    String getName();
}

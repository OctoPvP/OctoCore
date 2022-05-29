package net.octopvp.octocore.common.object;

import java.util.UUID;

public interface ServerInfo {
    String getServerName();

    String getCommitHash();

    String getCommitBranch();

    boolean isOnline(UUID uuid);
}

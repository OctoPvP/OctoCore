package net.octopvp.octocore.common.interfaces;

import java.util.UUID;

public interface ServerInfo {
    String getServerName();

    String getCommitHash();

    String getCommitBranch();

    boolean isOnline(UUID uuid);
}

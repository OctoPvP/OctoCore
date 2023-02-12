package net.octopvp.octocore.common.interfaces.manager;

import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.permissions.Rank;

import java.util.UUID;

public interface IRankManager {
    void save(Rank rank);

    Rank getRankById(UUID uuid);

    Rank getRankByName(String name);

    Rank getDefaultRank();

    boolean isLoadingRanks();

    boolean canGrant(SimplePlayerData playerData, Rank rank);

    void reloadRanks();
}

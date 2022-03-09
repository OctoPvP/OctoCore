package net.octopvp.octocore.paper.data;

import net.octopvp.octocore.paper.objects.PlayerData;

import java.util.UUID;

public interface DataProvider {
    void init();

    void disable();

    PlayerData getPlayerData(UUID uuid);

    void saveData(PlayerData data);

    PlayerData createNewProfile(UUID uuid, String name);
}

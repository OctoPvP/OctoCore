package net.octopvp.octocore.paper.data.impl;

import net.octopvp.octocore.paper.data.DataProvider;
import net.octopvp.octocore.paper.objects.PlayerData;

import java.util.UUID;

public class MongoDataProvider implements DataProvider {
    @Override
    public void init() {

    }

    @Override
    public void disable() {

    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return null;
    }

    @Override
    public void saveData(PlayerData data) {

    }

    @Override
    public PlayerData createNewProfile(UUID uuid, String name) {
        return null;
    }
}

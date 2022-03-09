package net.octopvp.octocore.paper.api;

import net.octopvp.octocore.paper.objects.PlayerData;

import java.util.UUID;

public interface OctoAPI {
    PlayerData getPlayerData(UUID uuid);
}

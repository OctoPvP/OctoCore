package net.octopvp.octocore.core.api;

import net.octopvp.octocore.core.objects.PlayerData;

import java.util.UUID;

public interface OctoAPI {
    PlayerData getPlayerData(UUID uuid);
}

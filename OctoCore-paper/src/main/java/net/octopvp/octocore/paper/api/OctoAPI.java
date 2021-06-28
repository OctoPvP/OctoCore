package net.octopvp.octocore.paper.api;

import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;

public interface OctoAPI {
    public PlayerData getPlayerData(UUID uuid);
}

package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VanishManager extends Manager implements Listener {
    @Getter
    private static VanishManager instance;

    @Getter
    private Map<UUID, Integer> vanished = new HashMap<>();

    @Override
    public void init(OctoCore plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    public boolean isVanished(Player player) {
        return isVanished(player.getUniqueId());
    }

    public boolean isVanished(UUID uuid) {
        return vanished.containsKey(uuid);
    }

    public int getVanishPriority(SimplePlayerData playerData, boolean... skipVanished) { // gets their current vanish priority
        if ((skipVanished.length == 0 || !skipVanished[0]) && vanished.containsKey(playerData.getUuid())) return vanished.get(playerData.getUuid());
        Rank rank = playerData.getHighestRank();
        if (rank != null) {
            return rank.getWeight() <= 0 ? 1 : rank.getWeight();
        }
        return 1;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData playerData = PlayerManager.getInstance().getData(event.getPlayer());
        if (playerData == null) return;
        if (playerData.isJoinVanished()) {
            event.setJoinMessage(null);
            vanish(event.getPlayer());
        } else {
            update(event.getPlayer());
        }
    }

    public void vanish(Player player) {
        vanish(player, -1);
    }

    public void vanish(Player player, int priority) {
        PlayerData data = PlayerManager.getInstance().getData(player);
        if (data == null) return;
        if (priority <= 0) priority = getVanishPriority(data);
        vanished.put(player.getUniqueId(), priority);
        update(player);
    }

    public void unvanish(Player player) {
        vanished.remove(player.getUniqueId());
        update(player);
    }

    public boolean canSee(Player viewer, Player target) {
        PlayerData viewerData = PlayerManager.getInstance().getData(viewer);
        PlayerData targetData = PlayerManager.getInstance().getData(target);
        if (targetData == null) {
            return true;
        }
        if (viewerData == null) {
            return !targetData.isVanished();
        }
        if (!targetData.isVanished()) {
            return true;
        }
        int viewerPriority = getVanishPriority(viewerData);
        int targetPriority = getVanishPriority(targetData);
        return viewerPriority >= targetPriority;
    }

    public void update(Player player) { // TODO: optimize this O(n) code
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) continue;
            PlayerData data = PlayerManager.getInstance().getData(online);
            if (data == null) continue;
            if (canSee(online, player)) {
                online.showPlayer(player);
            } else {
                online.hidePlayer(player);
            }
            if (canSee(player, online)) {
                player.showPlayer(online);
            } else {
                player.hidePlayer(online);
            }
        }
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }
}

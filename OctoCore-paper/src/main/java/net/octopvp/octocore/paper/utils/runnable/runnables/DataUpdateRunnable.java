package net.octopvp.octocore.paper.utils.runnable.runnables;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.json.JsonChain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class DataUpdateRunnable implements Runnable{
    @Override
    public void run() {
        JsonChain jsonChain = new JsonChain();
        jsonChain.addProperty("maxPlayers", Bukkit.getMaxPlayers());
        jsonChain.addProperty("whitelisted", Bukkit.hasWhitelist());
        jsonChain.addProperty("name", OctoCore.getServerName());
        jsonChain.addProperty("tps1", Bukkit.getServer().spigot().getTPS()[0]);
        jsonChain.addProperty("tps2", Bukkit.getServer().spigot().getTPS()[1]);
        jsonChain.addProperty("tps3", Bukkit.getServer().spigot().getTPS()[2]);
        jsonChain.addProperty("lastTick", System.currentTimeMillis());
        jsonChain.addProperty("players", StringUtils.getStringFromList(Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).collect(Collectors.toList())));

        OctoCore.getInstance().getRedisData().write(JedisAction.SERVER_DATA, jsonChain.get());
        for (PlayerData playerData : PlayerManager.getPlayerProfiles().values()) {
            if (playerData == null) continue;
            //playerData.reloadLuckPermsThings();
            playerData.setPlayTime(playerData.getPlayTime() + 1); //increment playtime by 1 second
            JsonChain playerDataChain = new JsonChain();
            playerDataChain.addProperty("name", playerData.getName());
            playerDataChain.addProperty("uuid", playerData.getUuid().toString());
            playerDataChain.addProperty("server", OctoCore.getServerName());
            playerDataChain.addProperty("rankName", playerData.getRankName());
            playerDataChain.addProperty("lastSeen", playerData.getLastLogin());
            playerDataChain.addProperty("firstJoined", playerData.getFirstJoin());
            playerDataChain.addProperty("vanished", playerData.isVanished());
            playerDataChain.addProperty("lastActivity", System.currentTimeMillis());
            playerDataChain.addProperty("lastServer", OctoCore.getServerName());

            playerDataChain.addProperty("staffChatAlerts", playerData.isStaffChatAlerts());
            playerDataChain.addProperty("adminChatAlerts", playerData.isAdminChatAlerts());
            playerDataChain.addProperty("reportAlerts", playerData.isReportAlerts());

            playerDataChain.addProperty("allTags",OctoCore.getGson().toJson(playerData.getAllowedTags()));

            OctoCore.getInstance().getRedisData().write(JedisAction.PLAYER_DATA, playerDataChain.get());
        }
    }
}

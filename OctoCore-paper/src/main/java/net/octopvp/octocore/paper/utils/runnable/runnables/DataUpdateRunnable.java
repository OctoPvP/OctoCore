package net.octopvp.octocore.paper.utils.runnable.runnables;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.PlayerDataPacket;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.GsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.stream.Collectors;

public class DataUpdateRunnable implements Runnable{
    @Override
    public void run() {

        Iterator<PunishPlayerData> playerDataIterator = PunishModule.getInstance().getProfileManager().getPlayerData().values().iterator();
        try {
            do {
                PunishPlayerData data = playerDataIterator.next();
                if (!data.isLoading()) {
                    data.updateBannedAlts();
                }
            } while (playerDataIterator.hasNext());
        } catch (Exception ignored) { }

        JsonBuilder jsonChain = new JsonBuilder().addProperty("maxPlayers", Bukkit.getMaxPlayers()).addProperty("whitelisted", Bukkit.hasWhitelist());
        jsonChain.addProperty("name", OctoCore.getServerName()).addProperty("tps1", Bukkit.getServer().spigot().getTPS()[0]).addProperty("tps2", Bukkit.getServer().spigot().getTPS()[1]);
        jsonChain.addProperty("tps3", Bukkit.getServer().spigot().getTPS()[2]).addProperty("lastTick", System.currentTimeMillis()).addProperty("players", StringUtils.getStringFromList(Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).collect(Collectors.toList())));

        new ServerUpdatePacket(jsonChain).send();
        for (PlayerData playerData : PlayerManager.getPlayerProfiles().values()) {
            if (playerData == null) continue;
            playerData.setLastDataSave(playerData.getLastDataSave() + 1);
            if (playerData.getLastDataSave() >= 120) //save every 2 mins
                playerData.save();
            playerData.setPlayTime(playerData.getPlayTime() + 1); //increment playtime by 1 second
            Player player = Bukkit.getPlayer(playerData.getUuid());
            String name = playerData.getName();
            if (name == null && playerData.isOnline())
                name = player.getName();
            JsonBuilder playerDataChain = new JsonBuilder()
                    .addProperty("name", name)
                    .addProperty("uuid", playerData.getUuid().toString())
                    .addProperty("server", OctoCore.getServerName())
                    .addProperty("rankName", playerData.getRankName())
                    .addProperty("lastSeen", playerData.getLastLogin())
                    .addProperty("firstJoined", playerData.getFirstJoin())
                    .addProperty("vanished", playerData.isVanished())
                    .addProperty("lastActivity", System.currentTimeMillis())
                    .addProperty("lastServer", OctoCore.getServerName())
                    .addProperty("staffChatAlerts", playerData.isStaffChatAlerts())
                    .addProperty("adminChatAlerts", playerData.isAdminChatAlerts())
                    .addProperty("reportAlerts", playerData.isReportAlerts())
                    .addProperty("allTags", GsonSerializer.serializeUUIDSet(playerData.getAllowedTagsID()))
                    .addProperty("permissions", OctoCore.getGson().toJson(playerData.getAllEffectivePermissions()));

            new PlayerDataPacket(playerDataChain).send();
        }
    }
}

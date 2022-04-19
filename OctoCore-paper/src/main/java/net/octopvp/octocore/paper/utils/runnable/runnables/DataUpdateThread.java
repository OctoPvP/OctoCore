package net.octopvp.octocore.paper.utils.runnable.runnables;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.PlayerDataPacket;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.CachedData;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.GsonSerializer;
import net.octopvp.octocore.paper.utils.GsonType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DataUpdateThread extends Thread {
    private final OctoCore plugin;

    @Override
    public void run() {
        while (true) {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                sleep(50 * 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!plugin.isEnabled()) return;

        JsonBuilder jsonChain = new JsonBuilder().addProperty("maxPlayers", Bukkit.getMaxPlayers()).addProperty("whitelisted", Bukkit.hasWhitelist());
        jsonChain.addProperty("name", OctoCore.getServerName()).addProperty("tps1", Bukkit.getServer().spigot().getTPS()[0]).addProperty("tps2", Bukkit.getServer().spigot().getTPS()[1]);
        jsonChain.addProperty("tps3", Bukkit.getServer().spigot().getTPS()[2]).addProperty("lastTick", System.currentTimeMillis()).addProperty("players", StringUtils.getStringFromList(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));

        new ServerUpdatePacket(jsonChain).send();
        for (PlayerData playerData : PlayerManager.getInstance().getPlayerProfiles().values()) {
            if (playerData == null) continue;
            playerData.setLastDataSave(playerData.getLastDataSave() + 1);
            if (playerData.getLastDataSave() >= 120) //save every 2 mins
                playerData.save();
            playerData.setPlayTime(playerData.getPlayTime() + 1); //increment playtime by 1 second
            Player player = Bukkit.getPlayer(playerData.getUuid());
            if (player != null) {
                playerData.setOp(player.isOp());
            }
            String name = playerData.getName();
            if (name == null && playerData.isOnline()) name = player.getName();
            /*
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
             */
            JsonBuilder pdata = new JsonBuilder()
                    .add("name", name)
                    .add("uuid", playerData.getUuid().toString())
                    .add("server", OctoCore.getServerName())
                    .add("firstJoined", playerData.getFirstJoin())
                    .add("lastServer", OctoCore.getServerName())
                    .add("address", playerData.getAddress())
                    .add("rank", playerData.getRankName())
                    .add("vanished", playerData.isVanished())
                    .add("staffChatAlerts", playerData.isStaffChatAlerts())
                    .add("adminChatAlerts", playerData.isAdminChatAlerts())
                    .add("reportAlerts", playerData.isReportAlerts())
                    .add("quited", false)
                    .add("lastSeen", playerData.getLastLogin())
                    .add("lastActivity", System.currentTimeMillis())
                    .add("allTags", GsonSerializer.serializeUUIDSet(playerData.getAllowedTagsID()))
                    .add("permissions", OctoCore.getGson().toJson(playerData.getAllEffectivePermissions()))
                    .add("negated-permissions", OctoCore.getGson().toJson(playerData.getAllNegatedPermissions()))
                    .add("alts", OctoCore.getGson().toJson(playerData.getAltsSafely(), GsonType.ALT))
                    .add("addresses", StringUtils.getStringFromList(playerData.getAddresses()))
                    .add("rankWeight", playerData.getHighestRank().getWeight())
                    .add("op", player.isOp());


            new PlayerDataPacket(pdata).send();
            if (player != null)
                new CachedData(player.getUniqueId()).update(playerData.save(true));
        }
    }

}

package net.octopvp.octocore.core.utils.runnable.runnables;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.packets.PlayerDataPacket;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        //JsonBuilder jsonChain = new JsonBuilder().addProperty("maxPlayers", Bukkit.getMaxPlayers()).addProperty("whitelisted", Bukkit.hasWhitelist());
        //jsonChain.addProperty("name", OctoCore.getServerName()).addProperty("tps1", Bukkit.getServer().spigot().getTPS()[0]).addProperty("tps2", Bukkit.getServer().spigot().getTPS()[1]);
        //jsonChain.addProperty("tps3", Bukkit.getServer().spigot().getTPS()[2]).addProperty("lastTick", System.currentTimeMillis()).addProperty("players", StringUtils.getStringFromList(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));

        new ServerUpdatePacket(OctoCore.getServerName()).send();
        for (PlayerData playerData : PlayerManager.getInstance().getPlayerProfiles().values()) {
            if (playerData == null) continue;
            playerData.setLastDataSave(playerData.getLastDataSave() + 1);
            if (playerData.getLastDataSave() >= 120) //save every 2 mins
                playerData.save();
            playerData.setPlayTime(playerData.getPlayTime() + 1); //increment playtime by 1 second
            Player player = Bukkit.getPlayer(playerData.getUuid());
            if (player == null) continue;
            if (player.getName() == null || player.getUniqueId() == null) continue;
            playerData.setOp(player.isOp());
            String name = playerData.getName();
            if (name == null && playerData.isOnline()) name = player.getName();
            /*
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
                    .add("messagesToggled", playerData.getMessageSettings().isMessagesOff())
                    .add("ignoreList", OctoCore.getGson().toJson(playerData.getMessageSettings().getIgnoreList(), GsonType.STRING_LIST))
                    .add("globalChat", playerData.getMessageSettings().isGlobalChat())
                    .add("coloredName", playerData.getCachedFormattedNameNoNickNoTag());

            if (player != null) {
                pdata.add("op", player.isOp());
            }
             */

            new PlayerDataPacket(playerData.getUuid(), OctoCore.getServerName(), name, OctoCore.getServerName(), playerData.getAddress(),
                    playerData.getRankName(), System.currentTimeMillis(), playerData.getFirstJoin(), playerData.getLastLogin(),
                    playerData.isVanished(), playerData.isStaffChatAlerts(), playerData.isAdminChatAlerts(), playerData.isReportAlerts(),
                    playerData.hasPermission(Permissions.STAFF), playerData.getAllowedTagsID(), playerData.getAllEffectivePermissions(),
                    playerData.getAllNegatedPermissions(), playerData.getAltsSafely(), playerData.getAddresses(),
                    playerData.getHighestRank().getWeight(), playerData.getMessageSettings(), playerData.getCachedFormattedNameNoNickNoTag(),
                    playerData.isOp()
            ).send();
            new DataCache(playerData.getUuid()).update(playerData.save(true));
        }
    }

}

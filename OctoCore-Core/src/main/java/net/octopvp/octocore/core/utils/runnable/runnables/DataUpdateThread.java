package net.octopvp.octocore.core.utils.runnable.runnables;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.packets.PlayerDataPacket;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class DataUpdateThread extends Thread {
    private final OctoCore plugin;

    public DataUpdateThread(OctoCore plugin) {
        super("OctoCore Data Update Thread");
        this.plugin = plugin;
    }

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

        if (OctoCoreCommon.getInstance().getRedisManager() == null) return;

        try {
            new ServerUpdatePacket(OctoCore.getServerName()).send();
            Map<UUID, PlayerData> map = PlayerManager.getInstance().getPlayerProfiles();
            if (map == null || map.isEmpty()) return;
            for (PlayerData playerData : map.values()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package net.octopvp.octocore.core.utils.runnable.runnables;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.redis.packets.ServerDataPacket;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DataUpdateThread extends Thread {
    private final OctoCore plugin;
    private static final long SLEEP_INTERVAL = 1000; // 1s
    private static long DATA_SAVE_INTERVAL = SLEEP_INTERVAL * 60 * 2; // 2m

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

        if (OctoCoreCommon.getInstance().getRedisManager() == null) {
            Logger.error("Redis manager is null, cannot update data");
            return;
        }

        try {
            ArrayList<OnlinePlayer> onlinePlayers = new ArrayList<>();
            // ArrayList<OnlinePlayer> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(player -> new OnlinePlayer(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress(), OctoCore.getServerName())).collect(Collectors.toCollection(ArrayList::new));
            for (PlayerData playerData : PlayerManager.getInstance().getPlayerProfiles().values()) {
                if (playerData == null) continue;
                playerData.setLastDataSave(playerData.getLastDataSave() + 1);
                if (playerData.getLastDataSave() > DATA_SAVE_INTERVAL) {
                    playerData.save();
                }
                playerData.setPlayTime(playerData.getPlayTime() + SLEEP_INTERVAL);
                Player player = Bukkit.getPlayer(playerData.getUuid());
                if (player == null || player.getName() == null || player.getUniqueId() == null) continue;
                playerData.setOp(player.isOp());
                OnlinePlayer onlinePlayer = new OnlinePlayer(playerData.getUuid(), playerData.getName(), playerData.getAddress(), OctoCore.getServerName(), playerData.isVanished(), VanishManager.getInstance().getVanishPriority(playerData));
                onlinePlayers.add(onlinePlayer);
                if (playerData.isVanished()) {
                    Component actionBar = Component.text("You are vanished with a priority of ").color(NamedTextColor.GREEN)
                            .append(Component.text(VanishManager.getInstance().getVanishPriority(playerData)).color(NamedTextColor.YELLOW));
                    OctoCore.getInstance().getServerImplementation().sendActionBar(player, actionBar);
                }
                new DataCache(playerData.getUuid()).update(playerData.getData());
            }
            double[] tps = Bukkit.getTPS();
            new ServerDataPacket(OctoCore.getServerName(), onlinePlayers, Bukkit.getMaxPlayers(), System.currentTimeMillis(), Bukkit.hasWhitelist(), tps[0], tps[1], tps[2], false).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package net.octopvp.octocore.core.utils.runnable.runnables;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.redis.packets.ServerDataPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

        if (OctoCoreCommon.getInstance().getRedisManager() == null) {
            Logger.error("Redis manager is null, cannot update data");
            return;
        }

        try {
            double[] tps = Bukkit.getTPS();
            ArrayList<OnlinePlayer> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(player -> new OnlinePlayer(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress(), OctoCore.getServerName())).collect(Collectors.toCollection(ArrayList::new));
            new ServerDataPacket(OctoCore.getServerName(), onlinePlayers, Bukkit.getMaxPlayers(), System.currentTimeMillis(), Bukkit.hasWhitelist(), tps[0], tps[1], tps[2], false).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

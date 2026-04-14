package net.octopvp.octocore.rpg.runnable;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataUpdateRunnable extends BukkitRunnable {
    private boolean tickPlayerData = false;

    @Override
    public void run() {
        tickPlayerData = !tickPlayerData;
        Set<UUID> done = new HashSet<>();
        
        RPGPlayerManager.getInstance().getDataMap().forEach((uuid, playerData) -> {
            if (tickPlayerData) {
                playerData.update();
            }
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                playerData.updateActionBar(player);
                done.add(uuid);
            }
        });
        
        /* Optional: Handle missing data kicks if needed
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!done.contains(onlinePlayer.getUniqueId())) {
                // Not loaded yet
            }
        }
        */
    }
}

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
    private int saveTicks = 0;

    @Override
    public void run() {
        tickPlayerData = !tickPlayerData;
        saveTicks++;

        RPGPlayerManager.getInstance().getDataMap().forEach((uuid, playerData) -> {
            if (tickPlayerData) {
                playerData.update();
            }
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                playerData.updateActionBar(player);
            }
        });

        // Save all data every 10 seconds (100 ticks since registered at 2L)
        if (saveTicks >= 100) {
            saveTicks = 0;
            net.octopvp.octocore.rpg.OctoRPG.getInstance().getServer().getScheduler().runTaskAsynchronously(net.octopvp.octocore.rpg.OctoRPG.getInstance(), () -> {
                RPGPlayerManager.getInstance().saveAll();
            });
        }
    }
}

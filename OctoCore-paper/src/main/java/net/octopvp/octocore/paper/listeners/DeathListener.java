package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().isDead()) {
            if (OctoCore.getInstance().getConfig().getBoolean("settings.auto-respawn")) {
                e.getEntity().setFireTicks(0);
                for (PotionEffect activePotionEffect : e.getEntity().getActivePotionEffects()) {
                    e.getEntity().removePotionEffect(activePotionEffect.getType());
                }
                e.getEntity().spigot().respawn();
            }
        }
    }
}

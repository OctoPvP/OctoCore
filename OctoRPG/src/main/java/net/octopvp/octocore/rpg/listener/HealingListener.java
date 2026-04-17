package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealingListener implements Listener {
    public HealingListener(OctoRPG plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
            if (data != null && data.hasBlight()) {
                event.setCancelled(true);
                player.sendActionBar(CC.translate("&d&lBLIGHTED &7- &cHealing Blocked!"));
                player.getWorld().spawnParticle(org.bukkit.Particle.ENTITY_EFFECT, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.05, org.bukkit.Color.PURPLE);
            }
        }
    }
}

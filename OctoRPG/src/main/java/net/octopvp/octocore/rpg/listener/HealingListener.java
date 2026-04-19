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
        if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity livingEntity)) return;

        boolean blighted = false;
        RPGPlayerData playerData = null;
        if (livingEntity instanceof Player player) {
            playerData = RPGPlayerManager.getInstance().getData(player.getUniqueId());
            if (playerData != null && playerData.hasBlight()) {
                blighted = true;
            }
        } else if (livingEntity.hasMetadata("rpg_blight")) {
            long expiry = livingEntity.getMetadata("rpg_blight").get(0).asLong();
            if (System.currentTimeMillis() < expiry) {
                blighted = true;
            } else {
                livingEntity.removeMetadata("rpg_blight", OctoRPG.getInstance());
            }
        }

        if (blighted) {
            event.setCancelled(true);
            livingEntity.getWorld().spawnParticle(org.bukkit.Particle.ENTITY_EFFECT, livingEntity.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.05, org.bukkit.Color.PURPLE);
            
            if (playerData != null && playerData.isDebug()) {
                ((Player)livingEntity).sendMessage(CC.translate("&7[&bRPG Debug&7] &cHealing Blocked! &7(Blight active)"));
            }
            return;
        }

        // Debug Log for successful healing
        if (playerData != null && playerData.isDebug()) {
            double amount = event.getAmount();
            String reason = event.getRegainReason().name();
            ((Player)livingEntity).sendMessage(CC.translate("&7[&bRPG Debug&7] &aHealed: &f+" + String.format("%.1f", amount) + " HP &7(Reason: " + reason + ")"));
        }
    }
}

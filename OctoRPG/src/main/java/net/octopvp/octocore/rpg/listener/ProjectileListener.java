package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.enchantment.effect.AccuracyEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ProjectileListener implements Listener {
    public ProjectileListener(OctoRPG plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // Start the homing task
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (org.bukkit.World world : plugin.getServer().getWorlds()) {
                for (Projectile projectile : world.getEntitiesByClass(Projectile.class)) {
                    if (projectile.isDead() || projectile.isOnGround()) continue;
                    
                    if (projectile.getPersistentDataContainer().has(AccuracyEffect.HOMING_KEY, PersistentDataType.INTEGER)) {
                        handleHoming(projectile);
                    }
                }
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            OctoRPG.getInstance().getEnchantmentManager().handleShoot(player, event, event.getBow());
        }
    }

    @EventHandler
    public void onHit(org.bukkit.event.entity.ProjectileHitEvent event) {
        event.getEntity().getPersistentDataContainer().remove(AccuracyEffect.HOMING_KEY);
    }

    private void handleHoming(Projectile projectile) {
        if (projectile.getShooter() == null || !(projectile.getShooter() instanceof LivingEntity shooter)) return;
        
        Entity bestTarget = null;
        double bestAngle = 0.5; // Threshold for "in front" (cosine of angle)

        Collection<Entity> nearby = projectile.getNearbyEntities(10, 10, 10);
        for (Entity entity : nearby) {
            if (entity == shooter || !(entity instanceof LivingEntity) || entity.isDead()) continue;
            
            Vector toTarget = entity.getLocation().toVector().subtract(projectile.getLocation().toVector()).normalize();
            Vector direction = projectile.getVelocity().normalize();
            
            double dot = direction.dot(toTarget);
            if (dot > bestAngle) {
                bestAngle = dot;
                bestTarget = entity;
            }
        }

        if (bestTarget != null) {
            Vector toTarget = bestTarget.getLocation().add(0, 1, 0).toVector().subtract(projectile.getLocation().toVector()).normalize();
            Vector currentVel = projectile.getVelocity();
            double speed = currentVel.length();
            
            // Gradually adjust velocity towards target
            Vector newVel = currentVel.add(toTarget.multiply(0.2)).normalize().multiply(speed);
            projectile.setVelocity(newVel);
            
            // Visual effect
            if (shooter instanceof Player player) {
                RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
                if (data != null && data.isDebug()) {
                    projectile.getWorld().spawnParticle(org.bukkit.Particle.CRIT, projectile.getLocation(), 1, 0, 0, 0, 0);
                }
            }
        }
    }
}

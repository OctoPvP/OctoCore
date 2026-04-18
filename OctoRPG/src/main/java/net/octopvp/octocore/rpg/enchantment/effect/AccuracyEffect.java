package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class AccuracyEffect implements EnchantmentEffect {
    public static final NamespacedKey HOMING_KEY = new NamespacedKey(OctoRPG.getInstance(), "homing_arrow");

    @Override
    public String getEnchantmentId() {
        return "accuracy";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        org.bukkit.Material type = item.getType();
        return type == org.bukkit.Material.BOW || type == org.bukkit.Material.CROSSBOW;
    }

    @Override
    public void onShoot(Player player, EntityShootBowEvent event, ItemStack item, int level) {
        double chance = level * 0.05;
        
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data != null && data.hasBadLuck()) {
            chance *= 0.5; // 50% reduction in luck
        }

        if (ThreadLocalRandom.current().nextDouble() < chance) {
            Entity projectile = event.getProjectile();
            if (projectile instanceof Projectile) {
                projectile.getPersistentDataContainer().set(HOMING_KEY, PersistentDataType.INTEGER, 1);
                
                // Increase speed by 5 m/s (0.25 blocks per tick)
                org.bukkit.util.Vector vel = projectile.getVelocity();
                double speed = vel.length();
                if (speed > 0) {
                    projectile.setVelocity(vel.multiply((speed + 0.25) / speed));
                }

                if (data != null && data.isDebug()) {
                    projectile.getWorld().spawnParticle(org.bukkit.Particle.ENCHANTED_HIT, projectile.getLocation(), 5, 0.1, 0.1, 0.1, 0.02);
                }
            }
        }
    }
}

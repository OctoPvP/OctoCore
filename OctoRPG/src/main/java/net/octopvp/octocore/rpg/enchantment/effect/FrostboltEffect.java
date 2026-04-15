package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class FrostboltEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "frostbolt";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        org.bukkit.Material type = item.getType();
        return type == org.bukkit.Material.BOW || type == org.bukkit.Material.CROSSBOW;
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        double chance = 0.05 + (level * 0.02);
        if (ThreadLocalRandom.current().nextDouble() < chance) {
            if (victim instanceof LivingEntity livingVictim) {
                if (livingVictim instanceof Player victimPlayer) {
                    RPGPlayerData victimData = RPGPlayerManager.getInstance().getData(victimPlayer.getUniqueId());
                    if (victimData != null) {
                        victimData.setStunned(20 * 2); // 2 seconds
                        victimPlayer.sendMessage(CC.translate("&cYou have been immobilized by Frostbolt!"));
                    }
                } else {
                    livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 2, 10, false, false, true));
                }
                livingVictim.getWorld().spawnParticle(Particle.SNOWFLAKE, livingVictim.getEyeLocation(), 15, 0.5, 0.5, 0.5, 0.05);
                player.sendMessage(CC.translate("&bYour Frostbolt immobilized " + victim.getName() + "!"));
            }
        }
    }
}

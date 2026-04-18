package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class BrillianceEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "brilliance";
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        if (!(victim instanceof LivingEntity livingVictim)) return;
        if (livingVictim.hasMetadata("NPC")) return;

        // 25% chance at Level 4 (Max) -> 6.25% per level
        double chance = level * 0.0625;
        
        RPGPlayerData attackerData = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (attackerData != null && attackerData.hasBadLuck()) {
            chance *= 0.5; // 50% reduction in luck
        }

        if (ThreadLocalRandom.current().nextDouble() < chance) {
            int durationTicks = level * 20; // 1s per level
            livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationTicks, 0)); 
            
            // Notify the victim
            if (livingVictim instanceof Player victimPlayer) {
                victimPlayer.sendActionBar(net.octopvp.octocore.common.util.CC.translate("&e&lBLINDED! &7(" + level + "s)"));
            }
            
            // Visual effect
            livingVictim.getWorld().spawnParticle(org.bukkit.Particle.ENTITY_EFFECT, livingVictim.getLocation().add(0, 1.5, 0), 5, 0.2, 0.2, 0.2, 0.05, org.bukkit.Color.YELLOW);

            // Debug logging for the attacker
            if (attackerData != null && attackerData.isDebug()) {
                String victimName = (livingVictim instanceof Player p) ? p.getName() : livingVictim.getType().name();
                player.sendMessage(net.octopvp.octocore.common.util.CC.translate("&7[&bRPG Debug&7] &fApplied &eBLINDNESS &fto &d" + victimName + " &7(Brilliance Enchant)"));
            }
        }
    }
}

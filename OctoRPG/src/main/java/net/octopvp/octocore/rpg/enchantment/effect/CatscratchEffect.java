package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.common.util.CC;
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

import java.util.ArrayList;
import java.util.List;

public class CatscratchEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "catscratch";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        org.bukkit.Material type = item.getType();
        String name = type.name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE") || type == org.bukkit.Material.BOW || type == org.bukkit.Material.CROSSBOW;
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        if (!(victim instanceof LivingEntity livingVictim)) return;

        // 1. Handle standard Potion debuffs
        List<PotionEffect> toUpdate = new ArrayList<>();
        for (PotionEffect effect : livingVictim.getActivePotionEffects()) {
            if (isDebuff(effect.getType())) {
                toUpdate.add(effect);
            }
        }

        for (PotionEffect old : toUpdate) {
            // Remove old and add new with increased amplifier
            livingVictim.removePotionEffect(old.getType());
            livingVictim.addPotionEffect(new PotionEffect(
                    old.getType(),
                    old.getDuration(),
                    old.getAmplifier() + level,
                    old.isAmbient(),
                    old.hasParticles(),
                    old.hasIcon()
            ));
        }

        // 2. Handle custom RPG debuffs
        int extensionSeconds = level; // 1 second per level
        if (livingVictim instanceof Player victimPlayer) {
            RPGPlayerData victimData = RPGPlayerManager.getInstance().getData(victimPlayer.getUniqueId());
            if (victimData != null) {
                if (victimData.hasBlight()) victimData.applyBlight((int) ((victimData.getBlightUntil() - System.currentTimeMillis()) / 1000) + extensionSeconds);
                if (victimData.hasBadLuck()) victimData.applyBadLuck((int) ((victimData.getBadLuckUntil() - System.currentTimeMillis()) / 1000) + extensionSeconds);
                if (victimData.getStunned() > 0) victimData.setStunned(victimData.getStunned() + (extensionSeconds * 10)); // Stun ticks are 0.1s each
            }
        } else {
            // Handle metadata debuffs for mobs (currently only Blight)
            if (livingVictim.hasMetadata("rpg_blight")) {
                long currentExpiry = livingVictim.getMetadata("rpg_blight").get(0).asLong();
                if (currentExpiry > System.currentTimeMillis()) {
                    long newExpiry = currentExpiry + (extensionSeconds * 1000L);
                    livingVictim.setMetadata("rpg_blight", new org.bukkit.metadata.FixedMetadataValue(net.octopvp.octocore.rpg.OctoRPG.getInstance(), newExpiry));
                }
            }
        }

        // Debug logging
        RPGPlayerData attackerData = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (attackerData != null && attackerData.isDebug()) {
            int totalCustom = 0;
            if (livingVictim instanceof Player p) {
                RPGPlayerData vData = RPGPlayerManager.getInstance().getData(p);
                if (vData != null) {
                    if (vData.hasBlight()) totalCustom++;
                    if (vData.hasBadLuck()) totalCustom++;
                    if (vData.getStunned() > 0) totalCustom++;
                }
            } else if (livingVictim.hasMetadata("rpg_blight")) totalCustom++;

            if (!toUpdate.isEmpty() || totalCustom > 0) {
                player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fCatscratch LVL " + level + " boosted &c" + (toUpdate.size() + totalCustom) + " &fdebuffs on &d" + victim.getName()));
            }
        }
    }

    private boolean isDebuff(PotionEffectType type) {
        // Broad list of harmful/negative potion effects
        return type.equals(PotionEffectType.SLOWNESS) ||
               type.equals(PotionEffectType.MINING_FATIGUE) ||
               type.equals(PotionEffectType.INSTANT_DAMAGE) ||
               type.equals(PotionEffectType.NAUSEA) ||
               type.equals(PotionEffectType.BLINDNESS) ||
               type.equals(PotionEffectType.HUNGER) ||
               type.equals(PotionEffectType.WEAKNESS) ||
               type.equals(PotionEffectType.POISON) ||
               type.equals(PotionEffectType.WITHER) ||
               type.equals(PotionEffectType.LEVITATION) ||
               type.equals(PotionEffectType.UNLUCK) ||
               type.equals(PotionEffectType.DARKNESS) ||
               type.equals(PotionEffectType.INFESTED) ||
               type.equals(PotionEffectType.OOZING) ||
               type.equals(PotionEffectType.WEAVING) ||
               type.equals(PotionEffectType.WIND_CHARGED);
    }
}

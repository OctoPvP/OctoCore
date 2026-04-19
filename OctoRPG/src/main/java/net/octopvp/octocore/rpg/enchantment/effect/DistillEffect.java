package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class DistillEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "distill";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        return item.getType().name().contains("POTION");
    }

    @Override
    public void onConsume(PlayerItemConsumeEvent event, ItemStack item, int level) {
        if (!(item.getItemMeta() instanceof PotionMeta meta)) return;
        
        Player player = event.getPlayer();
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        
        // 1. Collect all effects (Base + Custom)
        List<PotionEffect> allEffects = new ArrayList<>(meta.getCustomEffects());
        if (meta.getBasePotionType() != null) {
            allEffects.addAll(meta.getBasePotionType().getPotionEffects());
        }

        if (allEffects.isEmpty()) return;

        double multiplier = 1.0 + (0.05 * level); // +5% per level
        boolean debug = data != null && data.isDebug();

        for (PotionEffect effect : allEffects) {
            // interpretation: 'strength' = amplifier.
            // Amplifier is 0-indexed (0 = Level I, 1 = Level II).
            int oldAmp = effect.getAmplifier();
            int newAmplifier = (int) Math.round(((oldAmp + 1) * multiplier)) - 1;
            
            // Ensure it actually increased if level is high enough, or at least stayed same
            newAmplifier = Math.max(newAmplifier, oldAmp);

            PotionEffect boosted = new PotionEffect(
                    effect.getType(),
                    effect.getDuration(),
                    newAmplifier,
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            );
            player.addPotionEffect(boosted, true);

            // Debug Log
            if (debug) {
                String effectName = effect.getType().getName();
                if (effect.getType().equals(PotionEffectType.INSTANT_HEALTH)) {
                    int oldHealth = 6 * (oldAmp + 1);
                    int newHealth = 6 * (newAmplifier + 1);
                    player.sendMessage(CC.translate("&7[&bRPG Debug&7] &dDistill Calc: &f" + effectName + " &7(&a" + oldHealth + "HP &f-> &b" + newHealth + "HP&7) &fMult: &e" + String.format("%.2f", multiplier) + "x"));
                } else {
                    player.sendMessage(CC.translate("&7[&bRPG Debug&7] &dDistill Calc: &f" + effectName + " &7(&aLVL " + (oldAmp + 1) + " &f-> &bLVL " + (newAmplifier + 1) + "&7) &fMult: &e" + String.format("%.2f", multiplier) + "x"));
                }
            }
        }
        
        player.sendMessage(CC.translate("&7[&bRPG&7] &aDistill (+" + (int)(5 * level) + "% strength) enhanced your potion!"));
    }
}

package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

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
        
        List<PotionEffect> customEffects = meta.getCustomEffects();
        if (customEffects.isEmpty()) return;

        double multiplier = 1.0 + (0.05 * level); // +5% per level

        for (PotionEffect effect : customEffects) {
            // interpretation: 'strength' = amplifier.
            // Amplifier is 0-indexed (0 = Level I, 1 = Level II).
            // We scale the 'display level' (amp + 1) then subtract 1.
            int newAmplifier = (int) Math.round(((effect.getAmplifier() + 1) * multiplier)) - 1;
            
            // Ensure it actually increased if level is high enough, or at least stayed same
            newAmplifier = Math.max(newAmplifier, effect.getAmplifier());

            PotionEffect boosted = new PotionEffect(
                    effect.getType(),
                    effect.getDuration(),
                    newAmplifier,
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            );
            player.addPotionEffect(boosted, true);
        }
        
        player.sendMessage(CC.translate("&7[&bRPG&7] &aDistill (+" + (int)(5 * level) + "% strength) enhanced your potion!"));
    }
}

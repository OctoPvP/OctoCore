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
import org.bukkit.potion.PotionType;

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
        
        // 1. Collect all original effects (Base + Custom)
        List<PotionEffect> originalEffects = new ArrayList<>(meta.getCustomEffects());
        if (meta.getBasePotionType() != null) {
            originalEffects.addAll(meta.getBasePotionType().getPotionEffects());
        }

        if (originalEffects.isEmpty()) return;

        double multiplier = 1.0 + (0.05 * level); // +5% per level
        boolean debug = data != null && data.isDebug();

        // 2. Clear base potion and custom effects so they don't apply twice
        meta.clearCustomEffects();
        try {
            // Set to AWKWARD so it has no base effects of its own
            meta.setBasePotionType(PotionType.AWKWARD);
        } catch (Exception ignored) {}

        // 3. Add boosted effects back as custom effects
        for (PotionEffect effect : originalEffects) {
            int oldAmp = effect.getAmplifier();
            int newAmplifier = (int) Math.round(((oldAmp + 1) * multiplier)) - 1;
            newAmplifier = Math.max(newAmplifier, oldAmp);

            PotionEffect boosted = new PotionEffect(
                    effect.getType(),
                    effect.getDuration(),
                    newAmplifier,
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            );
            meta.addCustomEffect(boosted, true);

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
        
        // 4. Update the item meta - Minecraft will now apply these modified effects when the event finishes
        item.setItemMeta(meta);
        //TODO set the potion to awkward that way we dont get a double heal
        
        player.sendMessage(CC.translate("&7[&bRPG&7] &aDistill (+" + (int)(5 * level) + "% strength) enhanced your potion!"));
    }
}

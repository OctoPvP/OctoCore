package net.octopvp.octocore.rpg.enchantment;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.enchantment.effect.AccuracyEffect;
import net.octopvp.octocore.rpg.enchantment.effect.BaneEffect;
import net.octopvp.octocore.rpg.enchantment.effect.BrillianceEffect;
import net.octopvp.octocore.rpg.enchantment.effect.FrostboltEffect;
import net.octopvp.octocore.rpg.enchantment.effect.LifestealEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentManager {
    private final Map<String, EnchantmentEffect> effects = new HashMap<>();

    public EnchantmentManager(OctoRPG plugin) {
        registerEffect(new FrostboltEffect());
        registerEffect(new LifestealEffect());
        registerEffect(new AccuracyEffect());
        registerEffect(new BaneEffect());
        registerEffect(new BrillianceEffect());
    }

    public void registerEffect(EnchantmentEffect effect) {
        effects.put(effect.getEnchantmentId().toLowerCase(), effect);
    }

    public Map<String, EnchantmentEffect> getEffects() {
        return effects;
    }

    public void handleHit(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        
        item.getEnchantments().forEach((enchantment, level) -> {
            NamespacedKey key = enchantment.getKey();
            if (key.getNamespace().equals("octorpg")) {
                EnchantmentEffect effect = effects.get(key.getKey().toLowerCase());
                if (effect != null && effect.isCompatible(item)) {
                    effect.onHitEntity(player, victim, event, item, level);
                }
            }
        });
    }

    public void handleShoot(Player player, org.bukkit.event.entity.EntityShootBowEvent event, ItemStack item) {
        if (item == null || item.getType().isAir()) return;

        item.getEnchantments().forEach((enchantment, level) -> {
            NamespacedKey key = enchantment.getKey();
            if (key.getNamespace().equals("octorpg")) {
                EnchantmentEffect effect = effects.get(key.getKey().toLowerCase());
                if (effect != null && effect.isCompatible(item)) {
                    effect.onShoot(player, event, item, level);
                }
            }
        });
    }
}

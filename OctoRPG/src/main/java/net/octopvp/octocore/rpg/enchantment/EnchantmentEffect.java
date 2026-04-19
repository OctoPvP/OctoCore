package net.octopvp.octocore.rpg.enchantment;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public interface EnchantmentEffect {
    String getEnchantmentId();

    default boolean isCompatible(ItemStack item) {
        return true;
    }

    default void onHitEntity(Player player, org.bukkit.entity.Entity victim, org.bukkit.event.entity.EntityDamageByEntityEvent event, ItemStack item, int level) {}

    default void onShoot(Player player, org.bukkit.event.entity.EntityShootBowEvent event, ItemStack item, int level) {}

    default void onAbilityUse(Player player, String abilityId, ItemStack item, int level) {}
}

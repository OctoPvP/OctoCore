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

    default void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {}
}

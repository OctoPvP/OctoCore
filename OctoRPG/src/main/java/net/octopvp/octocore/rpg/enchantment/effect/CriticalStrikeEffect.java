package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class CriticalStrikeEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "critical_strike";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE");
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        if (event.isCritical()) {
            event.setDamage(event.getDamage() + level);
        }
    }
}

package net.octopvp.octocore.rpg.item;

import net.octopvp.octocore.rpg.object.ArmorType;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    String getId();
    String getName();
    Material getMaterial();
    java.util.List<String> getLore();

    default java.util.List<String> getLore(ItemStack item) {
        return getLore();
    }

    String getDescription();
    Rarity getRarity();
    ItemType getItemType();

    default WeaponType getWeaponType() {
        return null;
    }

    default ArmorType getArmorType() {
        return null;
    }
    
    default void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item) {}
    default void onKillEntity(Player player, LivingEntity victim, EntityDeathEvent event, ItemStack item) {}
    default void onInteract(org.bukkit.event.player.PlayerInteractEvent event, ItemStack item) {}
    
    ItemStack build();
}

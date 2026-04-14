package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DirtSword extends BaseRPGItem {
    private final NamespacedKey killsKey = new NamespacedKey(OctoRPG.getInstance(), "dirt_sword_kills");

    @Override
    public String getId() {
        return "DIRT_SWORD";
    }

    @Override
    public String getName() {
        return "Dirt Sword";
    }

    @Override
    public Material getMaterial() {
        return Material.WOODEN_SWORD;
    }

    @Override
    public String getDescription() {
        return "An extremely weak training sword. Doubles your damage every 100 kills.";
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.WEAPON;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.SWORD;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.add(CC.WHITE + "Your Kills: " + CC.RED + "0");
        return lore;
    }

    @Override
    public ItemStack build() {
        ItemStack item = super.build();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(killsKey, PersistentDataType.INTEGER, 0);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void onKillEntity(Player player, LivingEntity victim, EntityDeathEvent event, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        int kills = meta.getPersistentDataContainer().getOrDefault(killsKey, PersistentDataType.INTEGER, 0) + 1;
        meta.getPersistentDataContainer().set(killsKey, PersistentDataType.INTEGER, kills);
        
        List<String> newLore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String s : meta.getLore()) {
                if (CC.strip(s).toLowerCase().contains("kills:")) {
                    newLore.add(CC.WHITE + "Your Kills: " + CC.RED + kills);
                } else {
                    newLore.add(s);
                }
            }
        }
        meta.setLore(newLore);
        
        item.setItemMeta(meta);
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        int kills = meta.getPersistentDataContainer().getOrDefault(killsKey, PersistentDataType.INTEGER, 0);
        int multiplier = kills / 100;
        
        if (multiplier > 0) {
            event.setDamage(event.getDamage() * multiplier);
        } else {
            event.setDamage(0.5);
        }
    }
}

package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DirtSword implements CustomItem {
    private final NamespacedKey idKey = new NamespacedKey(OctoRPG.getInstance(), "item_id");
    private final NamespacedKey killsKey = new NamespacedKey(OctoRPG.getInstance(), "dirt_sword_kills");

    @Override
    public String getId() {
        return "DIRT_SWORD";
    }

    @Override
    public String getName() {
        return CC.translate("&6Dirt Sword");
    }

    @Override
    public Material getMaterial() {
        return Material.WOODEN_SWORD;
    }

    @Override
    public List<String> getLore() {
        return CC.translate(List.of(
            "&7An extremely weak training sword.",
            "&7Doubles your damage every 100 kills.",
            "&fYour Kills: &c0"
        ));
    }

    @Override
    public ItemStack build() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getName());
            meta.setLore(getLore());
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, getId());
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
                    newLore.add(CC.translate("&fYour Kills: &c" + kills));
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
            event.setDamage(0.5); // Weak base damage if < 100 kills
        }
    }
}

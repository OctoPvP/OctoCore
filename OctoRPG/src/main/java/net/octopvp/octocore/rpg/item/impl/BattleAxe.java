package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BattleAxe implements CustomItem {
    private final NamespacedKey idKey = new NamespacedKey(OctoRPG.getInstance(), "item_id");

    @Override
    public String getId() {
        return "BATTLE_AXE";
    }

    @Override
    public String getName() {
        return CC.translate("&fBattle Axe");
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_AXE;
    }

    @Override
    public List<String> getLore() {
        return CC.translate(List.of(
            "&7A heavy hitting cleaver.",
            "&7Very slow attack speed, massive damage."
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
            item.setItemMeta(meta);
        }
        return item;
    }
}

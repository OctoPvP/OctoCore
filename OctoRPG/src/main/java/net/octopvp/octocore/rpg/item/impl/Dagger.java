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

public class Dagger implements CustomItem {
    private final NamespacedKey idKey = new NamespacedKey(OctoRPG.getInstance(), "item_id");

    @Override
    public String getId() {
        return "DAGGER";
    }

    @Override
    public String getName() {
        return CC.translate("&fDagger");
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public List<String> getLore() {
        return CC.translate(List.of(
            "&7A small stealth weapon.",
            "&7Very fast attack speed, low base damage."
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

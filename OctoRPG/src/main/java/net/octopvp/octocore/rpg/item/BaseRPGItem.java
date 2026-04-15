package net.octopvp.octocore.rpg.item;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.StatModifier;
import net.octopvp.octocore.rpg.util.LoreUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseRPGItem implements CustomItem {
    protected final NamespacedKey idKey = new NamespacedKey(OctoRPG.getInstance(), "item_id");

    @Override
    public List<String> getLore() {
        return getLore(null);
    }

    @Override
    public List<String> getLore(ItemStack item) {
        List<String> lore = new ArrayList<>();

        if (item != null && !item.getEnchantments().isEmpty()) {
            StringBuilder enchants = new StringBuilder();
            item.getEnchantments().forEach((enchantment, level) -> {
                String name = org.apache.commons.lang3.StringUtils.capitalize(enchantment.getKey().getKey().replace("_", " "));
                String color = net.octopvp.octocore.rpg.util.EnchantmentUtil.getRarity(enchantment).getColor();
                enchants.append(color).append(name).append(" ").append(net.octopvp.octocore.rpg.util.RomanNumeralUtil.toRoman(level)).append(", ");
            });
            String enchantString = enchants.toString();
            enchantString = enchantString.substring(0, enchantString.length() - 2);
            for (String s : org.bukkit.util.ChatPaginator.wordWrap(enchantString, 30)) {
                lore.add(s);
            }
            lore.add("");
        }

        StringBuilder typeString = new StringBuilder();
        typeString.append(getItemType().name());
        if (getWeaponType() != null) {
            typeString.append(" (").append(getWeaponType().name()).append(")");
        } else if (getArmorType() != null) {
            typeString.append(" (").append(getArmorType().name()).append(")");
        }
        lore.add(CC.WHITE + typeString.toString());
        lore.add("");

        StatModifier holding = getStatModifierWhenHolding();
        if (holding != null) {
            List<String> list = LoreUtils.getStatInfo(holding);
            if (!list.isEmpty()) {
                lore.add(CC.GRAY + "When Holding: ");
                lore.addAll(list);
                lore.add("");
            }
        }

        StatModifier inventory = getStatModifierWhenInInventory();
        if (inventory != null) {
            List<String> list = LoreUtils.getStatInfo(inventory);
            if (!list.isEmpty()) {
                lore.add(CC.GRAY + "When In Inventory: ");
                lore.addAll(list);
                lore.add("");
            }
        }

        String desc = getDescription();
        if (desc != null && !desc.isEmpty()) {
            for (String s : org.bukkit.util.ChatPaginator.wordWrap(CC.translate(desc), 30)) {
                lore.add(CC.GRAY + s);
            }
            lore.add("");
        }

        lore.add(getRarity().getColor() + getRarity().getName());
        return lore;
    }

    public StatModifier getStatModifierWhenHolding() {
        return null;
    }

    public StatModifier getStatModifierWhenInInventory() {
        return null;
    }

    @Override
    public ItemStack build() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getRarity().getColor() + getName());
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, getId());
            item.setItemMeta(meta); // Set meta before building lore
            
            meta = item.getItemMeta();
            meta.setLore(getLore(item));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        return item;
    }
}

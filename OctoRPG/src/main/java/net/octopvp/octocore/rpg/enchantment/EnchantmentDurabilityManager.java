package net.octopvp.octocore.rpg.enchantment;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentDurabilityManager {
    private final OctoRPG plugin;
    private final NamespacedKey usesKey;
    private static final int DEFAULT_MAX_USES = 250;

    public EnchantmentDurabilityManager(OctoRPG plugin) {
        this.plugin = plugin;
        this.usesKey = new NamespacedKey(plugin, "enchantment_uses");
    }

    public void handleUsage(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        
        boolean changed = false;
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment ench = entry.getKey();
            if (!ench.getKey().getNamespace().equals("octorpg")) continue;

            String keyStr = ench.getKey().getKey().toLowerCase();
            NamespacedKey specificKey = new NamespacedKey(plugin, "uses_" + keyStr);
            
            int uses = pdc.getOrDefault(specificKey, PersistentDataType.INTEGER, DEFAULT_MAX_USES);
            uses--;

            if (uses <= 0) {
                meta.removeEnchant(ench);
                pdc.remove(specificKey);
                changed = true;
            } else {
                pdc.set(specificKey, PersistentDataType.INTEGER, uses);
                changed = true;
            }
        }

        if (changed) {
            item.setItemMeta(meta);
            plugin.getItemManager().rebuildLore(item);
        }
    }

    public List<String> getDurabilityLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        if (item == null || !item.hasItemMeta()) return lore;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        boolean hasAny = false;
        for (Enchantment ench : item.getEnchantments().keySet()) {
            if (!ench.getKey().getNamespace().equals("octorpg")) continue;
            
            if (!hasAny) {
                lore.add("");
                lore.add(CC.translate("&6Enchantment Charges:"));
                hasAny = true;
            }

            String keyStr = ench.getKey().getKey().toLowerCase();
            NamespacedKey specificKey = new NamespacedKey(plugin, "uses_" + keyStr);
            int uses = pdc.getOrDefault(specificKey, PersistentDataType.INTEGER, DEFAULT_MAX_USES);
            
            String name = ench.getKey().getKey();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            
            String color = "&a";
            if (uses < DEFAULT_MAX_USES / 4) color = "&c";
            else if (uses < DEFAULT_MAX_USES / 2) color = "&e";

            lore.add(CC.translate(" &7- " + name + ": " + color + uses + "&7/" + DEFAULT_MAX_USES));
        }
        
        return lore;
    }
}

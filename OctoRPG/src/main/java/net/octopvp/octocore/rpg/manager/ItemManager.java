package net.octopvp.octocore.rpg.manager;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class ItemManager implements Listener {
    private final Map<String, CustomItem> customItems = new HashMap<>();
    private final NamespacedKey idKey;

    public ItemManager(OctoRPG plugin) {
        this.idKey = new NamespacedKey(plugin, "item_id");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerItem(CustomItem item) {
        customItems.put(item.getId(), item);
    }

    public CustomItem getCustomItemById(String id) {
        return customItems.get(id);
    }

    public Map<String, CustomItem> getCustomItems() {
        return customItems;
    }

    public CustomItem getCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        
        String id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (id == null) return null;
        
        return customItems.get(id);
    }

    public void rebuildLore(Player player, ItemStack item) {
        CustomItem customItem = getCustomItem(item);
        if (customItem == null) return;
        
        // Active Enforcement: Remove illegal enchantments
        boolean changed = false;
        for (org.bukkit.enchantments.Enchantment ench : new java.util.HashSet<>(item.getEnchantments().keySet())) {
            if (ench.getKey().getNamespace().equals("octorpg")) {
                net.octopvp.octocore.rpg.enchantment.EnchantmentEffect effect = OctoRPG.getInstance().getEnchantmentManager().getEffects().get(ench.getKey().getKey().toLowerCase());
                if (effect != null && !effect.isCompatible(item)) {
                    item.removeEnchantment(ench);
                    changed = true;
                }
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        java.util.List<String> lore = customItem.getLore(item);

        // Add Enchantment Descriptions
        RPGPlayerData data = player == null ? null : RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (data != null && data.isShowEnchantments()) {
            boolean headerAdded = false;

            for (org.bukkit.enchantments.Enchantment ench : item.getEnchantments().keySet()) {
                String desc = net.octopvp.octocore.rpg.util.EnchantmentUtil.getDescription(ench);
                if (!desc.isEmpty()) {
                    if (!headerAdded) {
                        lore.add("");
                        lore.add(CC.translate("&b&lEnchantments:"));
                        headerAdded = true;                    }
                    String name = ench.getKey().getKey();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase().replace("_", " ");
                    lore.add(CC.translate(" &7" + name + ":"));
                    for (String wrapped : wrap(desc, 35)) {
                        lore.add(CC.translate("  &f" + wrapped));
                    }
                }
            }
        }

        lore.addAll(OctoRPG.getInstance().getEnchantmentDurabilityManager().getDurabilityLore(item));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private java.util.List<String> wrap(String text, int limit) {
        java.util.List<String> result = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() > limit) {
                result.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (line.length() > 0) {
            result.add(line.toString().trim());
        }
        return result;
    }

    public boolean canAddEnchantment(ItemStack item, org.bukkit.enchantments.Enchantment newEnch) {
        if (item == null || item.getType().isAir()) return false;
        
        int majorCount = 0;
        int minorCount = 0;
        
        for (org.bukkit.enchantments.Enchantment ench : item.getEnchantments().keySet()) {
            if (net.octopvp.octocore.rpg.util.EnchantmentUtil.isMajor(ench)) {
                majorCount++;
            } else {
                minorCount++;
            }
        }
        
        boolean addingMajor = net.octopvp.octocore.rpg.util.EnchantmentUtil.isMajor(newEnch);
        
        // Don't count if we are just upgrading an existing enchant
        if (item.containsEnchantment(newEnch)) {
            return true;
        }

        if (addingMajor) {
            return majorCount < 1; // Max 1 Major
        } else {
            return minorCount < 3; // Max 3 Minor
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        CustomItem customItem = getCustomItem(item);
        if (customItem != null) {
            customItem.onHitEntity(player, event.getEntity(), event, item);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            ItemStack item = player.getInventory().getItemInMainHand();
            CustomItem customItem = getCustomItem(item);
            if (customItem != null) {
                customItem.onKillEntity(player, event.getEntity(), event, item);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        CustomItem customItem = getCustomItem(item);
        if (customItem != null) {
            customItem.onInteract(event, item);
        }
    }

    @EventHandler
    public void onConsume(org.bukkit.event.player.PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        CustomItem customItem = getCustomItem(item);
        if (customItem != null) {
            customItem.onConsume(event, item);
        }
    }
}

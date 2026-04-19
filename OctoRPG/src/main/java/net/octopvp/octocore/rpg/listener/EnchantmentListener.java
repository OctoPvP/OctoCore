package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentListener implements Listener {
    private final OctoRPG plugin;

    public EnchantmentListener(OctoRPG plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null && result.getType() != Material.AIR) {
            if (event.getView().getPlayer() instanceof Player player) {
                plugin.getItemManager().rebuildLore(player, result);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAnvilCheck(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) return;
        
        int major = 0;
        int minor = 0;
        for (org.bukkit.enchantments.Enchantment ench : result.getEnchantments().keySet()) {
            if (ench.getKey().getNamespace().equals("octorpg")) {
                String id = ench.getKey().getKey();
                if (id.equals("comfort")) {
                    if (result.getType() != Material.ENCHANTED_BOOK) {
                        event.setResult(null);
                        return;
                    }
                }
                if (id.equals("volume")) {
                    if (!result.getType().name().contains("POTION")) {
                        event.setResult(null);
                        return;
                    }
                }
            }
            if (net.octopvp.octocore.rpg.util.EnchantmentUtil.isMajor(ench)) major++;
            else minor++;
        }
        
        if (major > 1 || minor > 3) {
            event.setResult(null); // Block the creation of illegal items
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Handle anvil result collection
        if (event.getInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            ItemStack result = event.getCurrentItem();
            if (result != null && result.getType() != Material.AIR) {
                plugin.getItemManager().rebuildLore(player, result);
            }
        }
        
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getCurrentItem() != null) {
            ItemStack target = event.getCurrentItem();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getItemManager().rebuildLore(player, target);
            });
        }
    }
}

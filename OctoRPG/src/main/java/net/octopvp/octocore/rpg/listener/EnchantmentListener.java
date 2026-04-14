package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.Material;
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
            plugin.getItemManager().rebuildLore(result);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // Handle anvil result collection
        if (event.getInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            ItemStack result = event.getCurrentItem();
            if (result != null && result.getType() != Material.AIR) {
                plugin.getItemManager().rebuildLore(result);
            }
        }
        
        // Potential handle for custom enchantment plugins that apply enchants via drag-and-drop
        // We delay it by 1 tick to allow the enchant to be applied first
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getCurrentItem() != null) {
            ItemStack target = event.getCurrentItem();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getItemManager().rebuildLore(target);
            });
        }
    }
}

package net.octopvp.octocore.rpg.manager;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
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

    public CustomItem getCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        
        String id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (id == null) return null;
        
        return customItems.get(id);
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
}

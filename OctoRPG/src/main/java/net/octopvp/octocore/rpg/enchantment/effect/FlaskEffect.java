package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FlaskEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "flask";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        return item.getType().name().contains("POTION");
    }

    @Override
    public void onConsume(PlayerItemConsumeEvent event, ItemStack item, int level) {
        // Use the existing durability system to manage uses
        // Potions don't normally have durability, so we initialize it if missing
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey specificKey = new NamespacedKey(OctoRPG.getInstance(), "uses_flask");
        
        // Default max uses for Flask: Level * 2
        int maxUses = level * 2;
        int uses = pdc.getOrDefault(specificKey, PersistentDataType.INTEGER, maxUses);
        
        uses--;

        if (uses > 0) {
            // Update durability and KEEP the potion (don't let it turn into a bottle)
            pdc.set(specificKey, PersistentDataType.INTEGER, uses);
            item.setItemMeta(meta);
            
            // Rebuild lore to show new charge count
            OctoRPG.getInstance().getItemManager().rebuildLore(event.getPlayer(), item);
            
            // IMPORTANT: setReplacement tells Paper what item the player should have AFTER consuming.
            // By setting it to the current item, we keep the potion in hand.
            event.setReplacement(item);
        } else {
            // Out of charges: The enchantment is essentially 'used up'
            // We let the event proceed normally, which will turn the potion into a glass bottle.
            // But we'll send a message that the flask is empty.
            event.getPlayer().sendMessage(net.octopvp.octocore.common.util.CC.translate("&cYour Flask enchantment has been depleted!"));
        }
    }
}

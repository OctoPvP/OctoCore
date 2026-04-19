package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RPGDebugCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be used by players.");
            return;
        }
        if (!player.hasPermission("octorpg.admin")) {
            player.sendMessage(CC.RED + "No permission.");
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("setdurability")) {
            int durability = 1;
            if (args.length > 1) {
                try {
                    durability = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(CC.RED + "Invalid durability value.");
                    return;
                }
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                player.sendMessage(CC.RED + "You must be holding an item.");
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            boolean changed = false;

            for (Enchantment ench : item.getEnchantments().keySet()) {
                if (ench.getKey().getNamespace().equals("octorpg")) {
                    String keyStr = ench.getKey().getKey().toLowerCase();
                    NamespacedKey specificKey = new NamespacedKey(OctoRPG.getInstance(), "uses_" + keyStr);
                    pdc.set(specificKey, PersistentDataType.INTEGER, durability);
                    changed = true;
                }
            }

            if (changed) {
                item.setItemMeta(meta);
                OctoRPG.getInstance().getItemManager().rebuildLore(player, item);
                player.sendMessage(CC.GREEN + "Set all RPG enchantment durability on your item to " + durability + ".");
            } else {
                player.sendMessage(CC.RED + "This item has no RPG enchantments.");
            }
            return;
        }
        
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data != null) {
            data.setDebug(!data.isDebug());
            RPGPlayerManager.getInstance().saveData(data);
            player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fDebug mode " + (data.isDebug() ? "&aEnabled" : "&cDisabled")));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return List.of("setdurability");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setdurability")) {
            return List.of("1", "10", "100", "250");
        }
        return List.of();
    }
}

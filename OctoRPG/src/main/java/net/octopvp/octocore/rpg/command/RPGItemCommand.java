package net.octopvp.octocore.rpg.command;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RPGItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            if (args.length == 0) {
                player.sendMessage("Usage: /rpgitem <id>");
                return true;
            }
            String id = args[0].toUpperCase();
            CustomItem item = OctoRPG.getInstance().getItemManager().getCustomItemById(id);
            if (item != null) {
                player.getInventory().addItem(item.build());
                player.sendMessage("You received a " + item.getName() + "!");
            } else {
                player.sendMessage("Item not found: " + id);
            }
            return true;
        }
        return false;
    }
}

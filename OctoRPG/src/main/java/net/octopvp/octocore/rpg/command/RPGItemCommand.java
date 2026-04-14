package net.octopvp.octocore.rpg.command;

import net.octopvp.octocore.rpg.item.impl.DirtSword;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RPGItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            player.getInventory().addItem(new DirtSword().build());
            player.sendMessage("You received a Dirt Sword!");
            return true;
        }
        return false;
    }
}

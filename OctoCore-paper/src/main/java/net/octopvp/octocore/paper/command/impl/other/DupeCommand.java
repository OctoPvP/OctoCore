package net.octopvp.octocore.paper.command.impl.other;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DupeCommand {
    @Command(name = "dupethis")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        Player player = sender.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        player.getInventory().addItem(item);
        return CommandResult.SUCCESS;
    }
}

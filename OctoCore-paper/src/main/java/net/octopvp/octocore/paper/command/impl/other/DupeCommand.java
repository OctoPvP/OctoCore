package net.octopvp.octocore.paper.command.impl.other;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DupeCommand extends BaseCommand {
    @Command(name = "dupethis", permission = Permission.ADMIN, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        Player player = sender.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        player.getInventory().addItem(item);
        return CommandResult.SUCCESS;
    }
}

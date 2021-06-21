package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Bukkit;

public class TrashCommand extends BaseCommand {
    @Command(name = "trash",aliases = {"garbage"},permission = Permission.TRASH,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.getPlayer().openInventory(Bukkit.createInventory(null,54));
        return CommandResult.SUCCESS;
    }
}

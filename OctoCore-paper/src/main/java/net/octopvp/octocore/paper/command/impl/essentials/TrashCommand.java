package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.Bukkit;

public class TrashCommand {
    @Command(name = "trash", aliases = {"garbage"})
    @Permission(Permissions.TRASH)
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        sender.getPlayer().openInventory(Bukkit.createInventory(null, 54));
        return CommandResult.SUCCESS;
    }
}

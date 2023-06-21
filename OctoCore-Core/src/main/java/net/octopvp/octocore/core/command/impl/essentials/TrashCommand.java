package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TrashCommand {
    @Command(name = "trash", aliases = {"garbage"})
    @Permission(Permissions.TRASH)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        sender.getPlayer().openInventory(Bukkit.createInventory(null, 54, "Trash"));
        return CommandResult.SUCCESS;
    }
}

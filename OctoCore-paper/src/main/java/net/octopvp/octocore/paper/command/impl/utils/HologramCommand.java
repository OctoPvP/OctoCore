package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class HologramCommand {
    @Command(name = "hologram")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {

        return CommandResult.SUCCESS;
    }
}

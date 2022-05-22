package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class HologramCommand {
    @Command(name = "hologram", playerOnly = true, permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {

        return CommandResult.SUCCESS;
    }
}

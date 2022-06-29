package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.debug.Debugger;

public class DebugCommand {
    @Command(name = "debugexp")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        String expression = StringUtils.arraytoString(args);
        new Debugger(sender.getCommandSender()).execute(expression);
        return CommandResult.SUCCESS;
    }

    @Command(name = "debug")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult executeDbg(Sender sender) {
        if (Logger.getDebugPlayers().contains(sender.getUniqueId()))
            Logger.getDebugPlayers().remove(sender.getUniqueId());
        else
            Logger.getDebugPlayers().add(sender.getUniqueId());
        sender.sendMessage(CC.GREEN + "Debug mode " + (Logger.getDebugPlayers().contains(sender.getUniqueId()) ? "enabled" : "disabled"));
        return CommandResult.SUCCESS;
    }
}

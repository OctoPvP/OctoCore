package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.debug.Debugger;

public class DebugCommand extends BaseCommand {
    @Command(name = "debugexp", permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        String expression = StringUtils.arraytoString(args);
        new Debugger(sender).execute(expression);
        return CommandResult.SUCCESS;
    }

    @Command(name = "debug", permission = Permission.ADMIN, playerOnly = true)
    public CommandResult executeDbg(Sender sender, String[] args) {
        if (Logger.getDebugPlayers().contains(sender.getUniqueId()))
            Logger.getDebugPlayers().remove(sender.getUniqueId());
        else
            Logger.getDebugPlayers().add(sender.getUniqueId());
        sender.sendMessage(CC.GREEN + "Debug mode " + (Logger.getDebugPlayers().contains(sender.getUniqueId()) ? "enabled" : "disabled"));
        return CommandResult.SUCCESS;
    }
}

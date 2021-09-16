package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.debug.Debugger;

public class DebugCommand extends BaseCommand {
    @Command(name = "debug",permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        String expression = StringUtils.arraytoString(args);
        new Debugger(sender).execute(expression);
        return CommandResult.SUCCESS;
    }
}

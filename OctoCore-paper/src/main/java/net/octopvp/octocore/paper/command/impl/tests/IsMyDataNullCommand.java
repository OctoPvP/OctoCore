package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.Sender;

public class IsMyDataNullCommand extends BaseCommand {
    @Command(name = "ismydatanull", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        boolean exists = PlayerManager.getData(sender.getPlayer()) == null;
        sender.sendMessage(exists + "");
        return CommandResult.SUCCESS;
    }
}

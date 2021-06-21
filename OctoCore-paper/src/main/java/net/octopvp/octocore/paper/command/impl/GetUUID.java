package net.octopvp.octocore.paper.command.impl;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class GetUUID extends BaseCommand {
    @Command(name = "getuuid")
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(sender.getPlayer().getUniqueId().toString());
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}

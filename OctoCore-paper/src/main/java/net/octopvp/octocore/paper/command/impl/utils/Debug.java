package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class Debug implements BaseCommand {
    @Command(name = "debug")
    public CommandResult execute(Sender sender, String[] args) {
        if(Logger.debugPlayers.contains(sender.getPlayer())){
            Logger.debugPlayers.remove(sender.getPlayer());
            sender.sendMessage(CC.GREEN + "You will no longer receive debug messages.");
        }else{
            Logger.debugPlayers.add(sender.getPlayer());
            sender.sendMessage(CC.GREEN + "You will now receive debug messages including some stack traces.");
        }
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}

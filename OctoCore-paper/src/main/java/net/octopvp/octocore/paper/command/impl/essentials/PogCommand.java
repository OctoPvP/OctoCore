package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class PogCommand extends BaseCommand {
    @Command(name = "pog",aliases = {"poggers"},cooldown = 6969420)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(CC.GREEN + "POGGERS");
        return CommandResult.SUCCESS;
    }
}

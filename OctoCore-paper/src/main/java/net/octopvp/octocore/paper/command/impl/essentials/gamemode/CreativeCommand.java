package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class CreativeCommand implements BaseCommand {
    @Override
    public CommandResult execute(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}

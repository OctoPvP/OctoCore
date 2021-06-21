package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class SignInputTestCommand extends BaseCommand {
    @Override
    public CommandResult execute(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }
}

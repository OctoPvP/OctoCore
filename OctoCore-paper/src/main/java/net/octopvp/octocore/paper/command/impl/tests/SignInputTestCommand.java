package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class SignInputTestCommand {
    @Override
    public CommandResult execute(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }
}

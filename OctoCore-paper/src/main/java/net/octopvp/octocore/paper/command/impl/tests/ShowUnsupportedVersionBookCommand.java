package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.autoinit.BookManager;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;

public class ShowUnsupportedVersionBookCommand extends BaseCommand {
    @Command(name = "showunsupportedversionbook",permission = Permission.ADMIN,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        BookManager.showUnsupportedVerBook(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

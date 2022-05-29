package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.autoinit.BookManager;
import net.octopvp.octocore.paper.utils.Sender;

public class ShowUnsupportedVersionBookCommand {
    @Command(name = "showunsupportedversionbook")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        BookManager.showUnsupportedVerBook(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

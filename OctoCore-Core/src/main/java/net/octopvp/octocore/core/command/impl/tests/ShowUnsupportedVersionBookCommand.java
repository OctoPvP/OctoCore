package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.autoinit.BookManager;
import org.bukkit.entity.Player;

public class ShowUnsupportedVersionBookCommand {
    @Command(name = "showunsupportedversionbook")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(@Optional @DefaultSelf Player player) {
        BookManager.showUnsupportedVerBook(player);
        return CommandResult.SUCCESS;
    }
}

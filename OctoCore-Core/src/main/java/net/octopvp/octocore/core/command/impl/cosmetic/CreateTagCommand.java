package net.octopvp.octocore.core.command.impl.cosmetic;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.menus.tag.ManageTagMenu;
import org.bukkit.entity.Player;

public class CreateTagCommand {
    @Command(name = "createtag")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        new ManageTagMenu(sender).open(sender);
        return CommandResult.SUCCESS;
    }
}

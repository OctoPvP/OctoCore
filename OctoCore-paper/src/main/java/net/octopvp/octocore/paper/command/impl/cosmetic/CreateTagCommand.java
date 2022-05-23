package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.ManageTagMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class CreateTagCommand {
    @Command(name = "createtag")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        new ManageTagMenu(sender.getPlayer()).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

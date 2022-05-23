package net.octopvp.octocore.paper.command.impl.admin;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.TagAdminMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class TagAdminCommand {
    @Command(name = "tagadmin")
    @Permission(Permissions.TAG_ADMIN_MENU)
    public CommandResult execute(Sender sender) {
        new TagAdminMenu().open(sender);
        return CommandResult.SUCCESS;
    }
}

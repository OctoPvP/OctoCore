package net.octopvp.octocore.paper.command.impl.admin;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.TagAdminMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class TagAdminCommand extends BaseCommand {
    @Command(name = "tagadmin", permission = Permission.TAG_ADMIN_MENU)
    public CommandResult execute(Sender sender, String[] args) {
        new TagAdminMenu().open(sender);
        return CommandResult.SUCCESS;
    }
}

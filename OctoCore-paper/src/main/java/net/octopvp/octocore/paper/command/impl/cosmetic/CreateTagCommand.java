package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.ManageTagMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class CreateTagCommand {
    @Command(name = "createtag", permission = Permission.ADMIN, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        new ManageTagMenu(sender.getPlayer()).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

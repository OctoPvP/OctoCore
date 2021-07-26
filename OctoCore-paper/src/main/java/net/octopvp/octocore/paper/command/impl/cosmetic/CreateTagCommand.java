package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.CreateTagMenu;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;

public class CreateTagCommand extends BaseCommand {
    @Command(name = "createtag",permission = Permission.ADMIN,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        new CreateTagMenu(sender.getPlayer()).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

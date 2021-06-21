package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.MainTagMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class TagCommand extends BaseCommand {
    @Command(name = "tag",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        new MainTagMenu().open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

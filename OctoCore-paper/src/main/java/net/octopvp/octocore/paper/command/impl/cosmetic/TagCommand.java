package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.tag.MainTagMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class TagCommand {
    @Command(name = "tag")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        new MainTagMenu().open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

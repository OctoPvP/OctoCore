package net.octopvp.octocore.core.command.impl.cosmetic;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.menus.tag.MainTagMenu;
import net.octopvp.octocore.core.utils.Sender;

public class TagCommand {
    @Command(name = "tag")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        new MainTagMenu().open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}

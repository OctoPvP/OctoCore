package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.menus.impl.rank.EditRankMenu;
import net.octopvp.octocore.core.objects.permissions.Rank;
import net.octopvp.octocore.core.utils.Sender;

public class EditRankCommand {
    @Command(name = "editrank")
    @Permission(Permissions.EDIT_RANK)
    @PlayerOnly
    public CommandResult execute(Sender sender, @Required @Name("rank") Rank target) {
        if (target == null) {
            sender.sendMessage(CC.RED + "Could not find that rank!");
            return CommandResult.SUCCESS;
        }
        new EditRankMenu(target).open(sender);
        return CommandResult.SUCCESS;
    }
}

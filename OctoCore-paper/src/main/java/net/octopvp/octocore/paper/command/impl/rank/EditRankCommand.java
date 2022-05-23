package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.rank.EditRankMenu;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;

public class EditRankCommand {
    @Command(name = "editrank")
    @Permission(Permissions.EDIT_RANK)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(CC.RED + "Usage: /editrank <rank>");
            return CommandResult.SUCCESS;
        }
        Rank target = RankManager.getInstance().getRankByName(args[0]);
        if (target == null) {
            sender.sendMessage(CC.RED + "Could not find that rank!");
            return CommandResult.SUCCESS;
        }
        new EditRankMenu(target).open(sender);
        return CommandResult.SUCCESS;
    }
}

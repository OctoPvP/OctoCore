package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.rank.delete.ConfirmDeleteMenu;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.entity.Player;

public class DeleteRankCommand {
    @Command(name = "deleterank", aliases = {"delrank"})
    @Permission(Permissions.DELETE_RANK)
    public CommandResult execute(Sender sender, @Required Rank rank) {
        if (rank == null) {
            sender.sendMessage(CC.RED + "Could not find that rank!");
            return CommandResult.SUCCESS;
        }
        if (sender.getCommandSender() instanceof Player) {
            new ConfirmDeleteMenu(rank).open(sender);
        } else {
            RankManager.getInstance().delete(rank);
        }
        return CommandResult.SUCCESS;
    }
}

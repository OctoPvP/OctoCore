package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.menus.rank.delete.ConfirmDeleteMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteRankCommand {
    @Command(name = "deleterank", aliases = {"delrank"})
    @Permission(Permissions.DELETE_RANK)
    public CommandResult execute(CommandSender sender, @Required Rank rank, @Switch(value = "confirm", aliases = "c") boolean confirm) {
        if (rank == null) {
            sender.sendMessage(CC.RED + "Could not find that rank!");
            return CommandResult.SUCCESS;
        }
        if (sender instanceof Player && !confirm) {
            new ConfirmDeleteMenu(rank).open((Player) sender);
        } else {
            RankManager.getInstance().delete(rank);
        }
        return CommandResult.SUCCESS;
    }
}

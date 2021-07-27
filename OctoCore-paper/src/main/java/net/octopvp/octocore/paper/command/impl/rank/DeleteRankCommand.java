package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.rank.delete.ConfirmDeleteMenu;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.entity.Player;

public class DeleteRankCommand extends BaseCommand {
    @Command(name = "deleterank",aliases = {"delrank"},permission = Permission.DELETE_RANK)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length != 1){
            sender.sendMessage(CC.RED + "Usage: /deleterank <rank name>");
            return CommandResult.SUCCESS;
        }
        Rank targetRank = RankManager.getRankByName(args[0]);
        if (targetRank == null) {
            sender.sendMessage(CC.RED + "Could not find that rank!");
            return CommandResult.SUCCESS;
        }
        if (sender.getCommandSender() instanceof Player){
            new ConfirmDeleteMenu(targetRank).open(sender);
        }else{
            RankManager.delete(targetRank);
        }
        return CommandResult.SUCCESS;
    }
}

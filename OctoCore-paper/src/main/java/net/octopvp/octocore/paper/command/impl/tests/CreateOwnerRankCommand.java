package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.ChatColor;

public class CreateOwnerRankCommand extends BaseCommand {
    @Command(name = "createtestrank",permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        Rank rankData = new Rank();
        rankData.setName("Owner");
        rankData.setPrefix("&7[&4OWNER&7]");
        rankData.setColor(ChatColor.DARK_RED);
        rankData.setWeight(100);
        rankData.setRankType(RankType.STAFF);
        rankData.save();
        RankManager.reloadRanks();
        return CommandResult.SUCCESS;
    }
}

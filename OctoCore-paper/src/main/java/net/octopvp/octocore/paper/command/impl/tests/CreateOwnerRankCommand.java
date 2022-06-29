package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.enums.RankType;
import org.bukkit.ChatColor;

public class CreateOwnerRankCommand {
    @Command(name = "createtestrank")
    @Permission(Permissions.ADMIN)
    public CommandResult execute() {
        RankBuilder builder = new RankBuilder("Owner")
                .setPrefix("&7[&4OWNER&7]")
                .setColor(ChatColor.DARK_RED)
                .setWeight(100)
                .setRankType(RankType.STAFF);
        builder.build().save();
        RankManager.getInstance().reloadRanks();
        return CommandResult.SUCCESS;
    }
}

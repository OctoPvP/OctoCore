package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.builders.RankBuilder;
import net.octopvp.octocore.core.objects.enums.RankType;
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

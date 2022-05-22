package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RefreshSkinCommand {
    @Command(name = "refreshskin", permission = Permission.REFRESH_SKIN, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.canSee(sender.getPlayer())) { //prevent players from seeing vanished
                onlinePlayer.hidePlayer(sender.getPlayer());
                onlinePlayer.showPlayer(sender.getPlayer());
            }
        }
        return CommandResult.SUCCESS;
    }
}

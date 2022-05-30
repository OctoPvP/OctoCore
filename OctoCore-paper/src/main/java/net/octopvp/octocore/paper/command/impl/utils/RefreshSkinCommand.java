package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RefreshSkinCommand {
    @Command(name = "refreshskin")
    @Permission(Permissions.REFRESH_SKIN)
    public CommandResult execute(@Sender Player sender) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.canSee(sender)) { //prevent players from seeing vanished
                onlinePlayer.hidePlayer(sender);
                onlinePlayer.showPlayer(sender);
            }
        }
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.core.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RefreshSkinCommand {
    @Command(name = "refreshskin")
    @Permission(Permissions.REFRESH_SKIN)
    @Cooldown(10)
    public CommandResult execute(@Sender Player sender) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.canSee(sender)) { //prevent players from seeing vanished
                onlinePlayer.hidePlayer(sender);
                onlinePlayer.showPlayer(sender);
            }
        }
        sender.sendMessage(CC.GREEN + "Refreshed your skin! It is still recommended to relog.");
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SeenCommand {
    @Command(name = "seen", aliases = {"lastseen"}, usage = "<player>")
    @Permission(Permissions.SEEN)
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        String name = args[0];
        // OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        OfflineHelpers.OfflineInfo offlineInfo = OfflineHelpers.getOfflineInfo(name);
        if (OctoCore.getInstance().getServerManager().isOnline(name)) {
            sender.sendMessage(CC.GREEN + offlineInfo.getDisplayName() + " is currently online!");
            return CommandResult.SUCCESS;
        }
        UUID id = offlineInfo.getUuid();
        long lastSeen = OctoCore.getInstance().getPlayerManager().getLastSeen(id), now = System.currentTimeMillis();
        if (lastSeen == -1) {
            return CommandResult.INVALID_PLAYER;
        }
        long diff = now - lastSeen;
        long days = diff / 86400000;
        long hours = (diff % 86400000) / 3600000;
        long minutes = (diff % 3600000) / 60000;

        sender.sendMessage(CC.translate(StringUtils.replacePlaceholders("&a%1 was last seen &6%2 day(s), %3 hours, %4 minutes ago.", offlineInfo.getName(), days, hours, minutes)));
        return CommandResult.SUCCESS;
    }
}

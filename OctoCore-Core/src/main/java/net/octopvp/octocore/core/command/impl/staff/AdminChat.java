package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminChatPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;

public class AdminChat {
    @Command(name = "adminchat", aliases = {"ac"})
    @Permission(Permissions.ADMINCHAT)
    @PlayerOnly
    public CommandResult execute(Sender sender, @Optional @JoinStrings String message) {
        PlayerData playerData = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        if (message == null) {
            playerData.setStaffChat(!playerData.isStaffChat());
            sender.sendMessage((playerData.isStaffChat() ? Lang.ADMIN_CHAT_ENABLED : Lang.ADMIN_CHAT_DISABLED));
            if (playerData.isStaffChat()) {
                playerData.setStaffChat(false);
                sender.sendMessage(Lang.STAFF_CHAT_DISABLED);
            }
        } else {
            new AdminChatPacket(sender.getPlayer().getName(), OctoCore.getServerName(), message, sender.getPlayer().getUniqueId());
        }
        return CommandResult.SUCCESS;
    }
}

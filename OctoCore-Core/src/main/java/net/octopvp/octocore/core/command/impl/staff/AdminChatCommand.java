package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminChatPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;

public class AdminChatCommand {
    @Command(name = "adminchat", aliases = {"ac"})
    @Permission(Permissions.ADMINCHAT)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender, @Optional @JoinStrings String message) {
        PlayerData playerData = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        if (message == null) {
            playerData.setStaffChat(!playerData.isStaffChat());
            sender.sendMessage((playerData.isStaffChat() ? Lang.ADMIN_CHAT_ENABLED : Lang.ADMIN_CHAT_DISABLED) + "");
            if (playerData.isStaffChat()) {
                playerData.setStaffChat(false);
                sender.sendMessage(Lang.STAFF_CHAT_DISABLED.toString());
            }
        } else {
            new AdminChatPacket(sender.getPlayer().getName(), playerData.getFormattedName(false, sender, false), OctoCore.getServerName(), message, sender.getPlayer().getUniqueId()).send();
        }
        return CommandResult.SUCCESS;
    }
}

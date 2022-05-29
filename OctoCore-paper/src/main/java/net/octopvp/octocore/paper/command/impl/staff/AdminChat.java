package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminChatPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;

public class AdminChat {
    @Command(name = "adminchat", aliases = {"ac"})
    @Permission(Permissions.ADMINCHAT)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData playerData = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        if (args.length == 0) {
            playerData.setStaffChat(!playerData.isStaffChat());
            sender.sendMessage((playerData.isStaffChat() ? Lang.ADMIN_CHAT_ENABLED : Lang.ADMIN_CHAT_DISABLED));
            if (playerData.isStaffChat()) {
                playerData.setStaffChat(false);
                sender.sendMessage(Lang.STAFF_CHAT_DISABLED);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i == 0)
                    sb.append(args[i]);
                else sb.append(" ").append(args[i]);
            }
            new AdminChatPacket(sender.getPlayer().getName(), OctoCore.getServerName(), sb.toString(), sender.getPlayer().getUniqueId());
        }
        return CommandResult.SUCCESS;
    }
}

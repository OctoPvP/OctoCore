package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.staff.StaffChatPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;

public class StaffChat {
    @Command(name = "staffchat", aliases = {"sc"}, permission = Permission.STAFFCHAT, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData playerData = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        if (args.length == 0) {
            playerData.setStaffChat(!playerData.isStaffChat());
            sender.sendMessage((playerData.isStaffChat() ? Lang.STAFF_CHAT_ENABLED : Lang.STAFF_CHAT_DISABLED));
            if (playerData.isAdminChat()) {
                playerData.setAdminChat(false);
                sender.sendMessage(Lang.ADMIN_CHAT_DISABLED);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i == 0)
                    sb.append(args[i]);
                else sb.append(" ").append(args[i]);
            }
            new StaffChatPacket(sender.getPlayer().getName(), OctoCore.getServerName(), sb.toString(), sender.getPlayer().getUniqueId()).send();
        }
        return CommandResult.SUCCESS;
    }
}

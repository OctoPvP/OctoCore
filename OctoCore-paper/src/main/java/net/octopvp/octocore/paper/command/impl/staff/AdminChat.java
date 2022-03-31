package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminChatPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;

public class AdminChat extends BaseCommand {
    @Command(name = "adminchat", aliases = {"ac"}, permission = Permission.ADMINCHAT, playerOnly = true)
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

package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

public class StaffChat extends BaseCommand {
    @Command(name = "staffchat",aliases = {"sc"},permission = Permission.STAFFCHAT,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData playerData = PlayerManager.getProfile(sender.getPlayer().getUniqueId());
        if(args.length == 0){
            playerData.setStaffChat(!playerData.isStaffChat());
            sender.sendMessage((playerData.isStaffChat() ? Lang.STAFF_CHAT_ENABLED : Lang.STAFF_CHAT_DISABLED));
            if(playerData.isAdminChat()){
                playerData.setAdminChat(false);
                sender.sendMessage(Lang.ADMIN_CHAT_DISABLED);
            }
        }else{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if(i == 0)
                    sb.append(args[i]);
                else sb.append(" ").append(args[i]);
            }
            PlayerManager.sendStaffChat(sender.getPlayer(),sb.toString(), OctoCore.getServerName());
        }
        return CommandResult.SUCCESS;
    }
}

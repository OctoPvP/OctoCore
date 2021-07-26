package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SeenCommand extends BaseCommand {
    @Command(name = "seen",permission = Permission.SEEN,description = "When the player was last seen on the network",cooldown = 3)
    public CommandResult execute(Sender sender, String[] args) {
        // /seen Badbird5907
        if(StringUtils.isUuid(args[0])){
            PlayerData profile = PlayerManager.getProfileFromDB(UUID.fromString(args[0]));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(profile.getLastLogin());
            sender.sendMessage(Lang.COMMAND_SEEN_NOT_ONLINE.getMsg(args[0],sdf.format(resultdate)));
        }
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionInfoCommand extends BaseCommand {
    @Command(name = "haspermission",aliases = {"permissioninfo","perminfo"},permission = Permission.COMMAND_PERMISSION_INFO,usage = "<permission> [player]",description = "Shows information about a player's permission")
    public CommandResult execute(Sender sender, String[] args) {
        String target = "", permission = "";
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        //args[0] is the permission
        //args[1] is the target player
        if (args.length == 2) {
            permission = args[0];
            target = args[1];
        }
        if (args.length == 1) {
            if (!sender.isPlayer()){
                return CommandResult.INVALID_ARGS;
            }
            permission = args[0];
            target = sender.getName();
        }
        Player targetPlayer = Bukkit.getPlayer(target);
        PlayerData data = PlayerManager.getData(targetPlayer);
        PermissionResult result = data.getPermissionResult(permission);
        sender.sendMessage(CC.SEPARATOR);
        sender.sendMessage(CC.PRIMARY + "Permission Info For: " + CC.SECONDARY + target);
        sender.sendMessage(CC.PRIMARY + "Permission: " + CC.SECONDARY + permission);
        sender.sendMessage(CC.PRIMARY + "Allowed: " + (result.allowed() ? CC.GREEN + "Yes" : CC.RED + "No"));
        sender.sendMessage(CC.PRIMARY + "Reason: " + CC.SECONDARY + result.getReason());
        sender.sendMessage(CC.PRIMARY + "Data: " + CC.SECONDARY + result.getData());
        sender.sendMessage(CC.SEPARATOR);
        return CommandResult.SUCCESS;
    }
}

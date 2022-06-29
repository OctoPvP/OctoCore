package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class PermissionInfoCommand {
    @Command(name = "haspermission", aliases = {"permissioninfo", "perminfo"}, description = "Shows information about a player's permission")
    @PlayerOnly
    public CommandResult execute(Sender sender, @Required String permission, @Optional PlayerData target) {
        PermissionResult result = target.getPermissionResult(permission);
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

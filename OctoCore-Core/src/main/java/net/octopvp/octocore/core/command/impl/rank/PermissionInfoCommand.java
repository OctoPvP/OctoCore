package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.perms.PermissionCheckResult;
import net.octopvp.octocore.common.util.perms.PermissionManager;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class PermissionInfoCommand {
    @Command(name = "haspermission", aliases = {"permissioninfo", "perminfo"}, description = "Shows information about a player's permission")
    @PlayerOnly
    public CommandResult execute(@Sender Player sender, @Required String permission, @Optional @DefaultSelf PlayerData target, @Switch("p") boolean printNodes) {
        if (printNodes) {
            System.out.println("----------------------------");
            System.out.println("Final Nodes: ");
            PermissionManager.getInstance().printNodeMap(target.getFinalNodeTree());
            System.out.println("----------------------------");
            System.out.println("Rank Nodes: ");
            PermissionManager.getInstance().printNodeMap(target.getHighestRank().getFinalNodeTree());
            System.out.println("----------------------------");
        }
        PermissionCheckResult result = target.getPermissionResult(permission);
        sender.sendMessage(CC.SEPARATOR);
        sender.sendMessage(CC.PRIMARY + "Permission Info For: " + CC.SECONDARY + target.getDisplayName());
        sender.sendMessage(CC.PRIMARY + "Permission: " + CC.SECONDARY + permission);
        sender.sendMessage(CC.PRIMARY + "Allowed: " + (result.allowed() ? CC.GREEN + "Yes" : CC.RED + "No"));
        sender.sendMessage(CC.PRIMARY + "Reason: " + CC.SECONDARY + result.getReason());
        sender.sendMessage(CC.SEPARATOR);
        return CommandResult.SUCCESS;
    }
}

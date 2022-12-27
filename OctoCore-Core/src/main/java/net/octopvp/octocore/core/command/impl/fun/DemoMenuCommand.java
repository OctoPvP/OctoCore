package net.octopvp.octocore.core.command.impl.fun;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.trolls.DemoMenuTroll;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoMenuCommand {
    @Command(name = "demomenu")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(CommandSender sender, @Optional String target) {
        if (target == null) {
            DemoMenuTroll.getInstance().activate((Player) sender);
            return CommandResult.SUCCESS;
        }
        if (target.equalsIgnoreCase("all") || target.equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                DemoMenuTroll.getInstance().activate(player);
            }
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null)
                return CommandResult.PLAYER_NOT_FOUND;
            DemoMenuTroll.getInstance().activate(player);
        }
        return CommandResult.SUCCESS;
    }
}

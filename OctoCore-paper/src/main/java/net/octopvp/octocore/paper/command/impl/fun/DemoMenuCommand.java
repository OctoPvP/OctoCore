package net.octopvp.octocore.paper.command.impl.fun;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.trolls.DemoMenuTroll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DemoMenuCommand {
    @Command(name = "demomenu")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            DemoMenuTroll.getInstance().activate(sender.getPlayer());
            return CommandResult.SUCCESS;
        }
        String target = args[0];
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

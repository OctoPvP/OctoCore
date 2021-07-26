package net.octopvp.octocore.paper.command.impl.fun;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.trolls.DemoMenuTroll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DemoMenuCommand extends BaseCommand {
    @Command(name = "demomenu",permission = Permission.TROLL_DEMO_MENU)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0){
            new DemoMenuTroll().activate(sender.getPlayer());
            return CommandResult.SUCCESS;
        }
        String target = args[0];
        if (target.equalsIgnoreCase("all")){
            for (Player player : Bukkit.getOnlinePlayers()) {
                new DemoMenuTroll().activate(player);
            }
        }else{
            Player player = Bukkit.getPlayer(target);
            if (player == null)
                return CommandResult.PLAYER_NOT_FOUND;
            new DemoMenuTroll().activate(player);
        }
        return CommandResult.SUCCESS;
    }
}

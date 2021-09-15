package net.octopvp.octocore.paper.module.impl.auth;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ForceAuthCommand extends BaseCommand {
    @Command(name = "forceauth", permission = Permission.FORCE_AUTH,usage = "<player>")
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length == 1){
            if(AuthModule.isServerAuthEnabled()){
                Player player = Bukkit.getPlayer(args[0]);
                if(player == null)
                    return CommandResult.PLAYER_NOT_FOUND;
                AuthModule.force2fa(player);
            }else sender.sendMessage(CC.RED + "2fa is not enabled on this instance! (try going to hub or master server)!");
            return CommandResult.SUCCESS;
        }
        return CommandResult.INVALID_ARGS;
    }
}

package net.octopvp.octocore.paper.command.impl.fun;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.trolls.KaboomTroll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KaboomCommand extends BaseCommand {
    @Command(name = "kaboom",permission = Permission.KABOOM)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1){
            Player player;
            try{
                player = Bukkit.getPlayer(args[0]);
            } catch (Exception e) {
                return CommandResult.PLAYER_NOT_FOUND;
            }
            new KaboomTroll().activate(player);
            sender.sendMessage(CC.GREEN + "Launched " + player.getName());
            return CommandResult.SUCCESS;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            new KaboomTroll().activate(player);
            sender.sendMessage(CC.GREEN + "Launched " + player.getName());
        });
        return CommandResult.SUCCESS;
    }
}

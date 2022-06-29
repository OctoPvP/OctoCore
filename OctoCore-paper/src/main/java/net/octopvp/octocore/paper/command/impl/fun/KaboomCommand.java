package net.octopvp.octocore.paper.command.impl.fun;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.trolls.KaboomTroll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KaboomCommand {
    @Command(name = "kaboom")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender, @Optional Player player) {
        if (player != null) {
            KaboomTroll.getInstance().activate(player);
            sender.sendMessage(CC.GREEN + "Launched " + player.getName());
            return CommandResult.SUCCESS;
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            KaboomTroll.getInstance().activate(p);
            sender.sendMessage(CC.GREEN + "Launched " + p.getName());
        });
        return CommandResult.SUCCESS;
    }
}

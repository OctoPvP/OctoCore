package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.module.impl.punishments.menus.alts.PotentialAltsMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltsCommand {
    @Command(name = "alts", description = "View a player's alts")
    @Permission(Permissions.VIEW_HISTORY)
    public void execute(@Sender CommandSender sender, PlayerData target) {
        if (!target.isAltsLoaded())
            target.loadAlts(target.getLastSeenIp());
        if (target.getAlts().isEmpty()) {
            sender.sendMessage(CC.RED + "No alts found.");
            return;
        }
        if (sender instanceof Player) {
            new PotentialAltsMenu(target, null).open((Player) sender);
        } else {
            sender.sendMessage(CC.GREEN + "Alts of " + target.getName() + ":");
            for (Alt alt : target.getAlts()) {
                sender.sendMessage(CC.GRAY + " - " + CC.GREEN + alt.getName() + CC.GRAY + " (" + CC.GOLD + alt.getLastKnownAddress() + CC.GRAY + ")");
            }
        }
    }
}

package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.module.impl.punishments.menus.alts.PotentialAltsMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class AltsCommand {
    @PlayerOnly
    @Command(name = "alts", description = "View a player's alts")
    @Permission(Permissions.VIEW_HISTORY)
    public void execute(@Sender Player sender, PlayerData target) {
        if (!target.isAltsLoaded())
            target.loadAlts(target.getLastSeenIp());
        if (target.getAlts().isEmpty()) {
            sender.sendMessage(CC.RED + "No alts found.");
            return;
        }
        new PotentialAltsMenu(target, null).open(sender);
    }
}

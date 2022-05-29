package net.octopvp.octocore.paper.command.impl.punishments;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.menus.staffhistory.StaffHistoryMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StaffHistoryCommand {

    @Command(name = "staffhistory", aliases = {"staffhist", "staffh"}, usage = "<player>")
    @Permission(Permissions.PUNISHMENT_STAFF_HISTORY)
    @PlayerOnly
    public CommandResult execute(Sender sender, OfflinePlayer target) {
        Tasks.runAsync(() -> {
            Player player = sender.getPlayer();
            //OfflinePlayer target = Bukkit.getOfflinePlayer(PlayerManager.getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = PlayerManager.getInstance().getData(target.getUniqueId());
                new StaffHistoryMenu(targetData).open(player);
            } else {
                PlayerData targetData = PlayerManager.getInstance().getData(target.getUniqueId());

                if (targetData == null) {
                    return;
                }
                targetData.loadPunishmentsPerformed();
                new StaffHistoryMenu(targetData).open(player);
            }
        });
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.paper.command.impl.punishments;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.menus.staffhistory.StaffHistoryMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StaffHistoryCommand extends BaseCommand {

    @Command(name = "staffhistory", aliases = {"staffhist", "staffh"}, usage = "<player>", playerOnly = true, permission = Permission.PUNISHMENT_STAFF_HISTORY)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        Tasks.runAsync(() -> {
            Player player = sender.getPlayer();
            //OfflinePlayer target = Bukkit.getOfflinePlayer(PlayerManager.getFixedName(args[0]));
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
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

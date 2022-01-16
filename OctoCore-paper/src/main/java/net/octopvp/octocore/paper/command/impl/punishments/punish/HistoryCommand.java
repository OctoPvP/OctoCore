package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.menus.HistoryMenu;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class HistoryCommand extends BaseCommand {

    @Command(name = "history", permission = Permission.PUNISHMENT_HISTORY, aliases = {"c", "cpunishments", "checkpunishments", "hist", "check"})
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length == 0) {
                sender.sendMessage(CC.translate("&cUsage: /history <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(PunishModule.getInstance().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                sender.sendMessage(CC.translate("&aPlease wait..."));
                PunishModule.getInstance().getProfileManager().createPlayerData(target.getUniqueId(), target.getName());
                targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

                if (!targetData.hasPlayedBefore()) {
                    sender.sendMessage(Lang.HAVENT_PLAYED_BEFORE.toString());
                    PunishModule.getInstance().getProfileManager().unloadData(target.getUniqueId());
                    return;
                }

                targetData.getPunishData().load();
                targetData.load();
            }
            new HistoryMenu(targetData.getPunishData()).open(sender);
        });
        return CommandResult.SUCCESS;
    }
}

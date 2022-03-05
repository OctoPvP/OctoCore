package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishHistory;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BlacklistCommand extends BaseCommand {

    @Command(name = "blacklist", permission = Permission.PUNISHMENT_BLACKLIST, aliases = {"bl", "blplayer", "blacklistplayer"})
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /blacklist <player> <reason>"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(PunishModule.getInstance().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                PunishModule.getInstance().getProfileManager().createPlayerData(target.getUniqueId(), target.getName());
                targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
                targetData.load();
            }
            if (targetData.getPunishData().isBlacklisted()) {
                sender.sendMessage(Lang.BLACKLIST_ALREADY_BLACKLISTED.toString().replace("%name%", target.getName()));
                PunishModule.getInstance().getProfileManager().unloadData(target);
                return;
            }
            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Blacklisted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = new Punishment(targetData, PunishmentType.BLACKLIST);
            punishment.setSilent(silent);
            punishment.setPermanent(true);
            punishment.setIPRelative(true);
            punishment.setLast(true);
            punishment.setAddedBy(sender.getUniqueId());
            punishment.setAddedByName(sender.getName());
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            targetData.getPunishData().getPunishments().add(punishment);

            punishment.execute(sender);
            punishment.save();

            if (sender.isPlayer()) {
                Player player = sender.getPlayer();
                PlayerData playerData = PlayerManager.getData(player.getUniqueId());
                if (playerData == null) {
                    return;
                }
                PunishHistory punishHistory = new PunishHistory(sender.getName(), PunishmentType.BAN);
                punishHistory.setAddedAt(punishment.getAddedAt());
                punishHistory.setDuration(punishment.getDurationTime());
                punishHistory.setPermanent(punishment.isPermanent());
                punishHistory.setExecutor(sender.getName());
                punishHistory.setTarget(targetData.getPlayerName());
                punishHistory.setReason(punishment.getReason());
                punishHistory.setActive(punishment.isActive());
                punishHistory.setLast(punishment.isLast());
                punishHistory.setSilent(punishment.isSilent());
                punishHistory.setEnteredDuration(punishment.getEnteredDuration());

                playerData.getPunishmentsExecuted().add(punishHistory);
            }
        });
        return CommandResult.SUCCESS;
    }
}

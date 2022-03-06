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
import net.octopvp.octocore.paper.utils.DateUtils;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MuteIPCommand extends BaseCommand {
    @Command(name = "muteip", permission = Permission.PUNISHMENT_MUTE, aliases = {"tempipmute", "ipmute", "tempmuteip"})
    public CommandResult execute(Sender sender, String[] args) {
        if (true) {
            sender.sendMessage(CC.RED + "This command is still being implemented.");
            return CommandResult.SUCCESS;
        }
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /muteip <player> [duration] <reason> [-s]"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(PunishModule.getInstance().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                PunishModule.getInstance().getProfileManager().createPlayerData(target.getUniqueId(), target.getName());
                targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (targetData.getPunishData().isMuted()) {
                sender.sendMessage(Lang.MUTE_ALREADY_MUTED);
                PunishModule.getInstance().getProfileManager().unloadData(target);
                return;
            }

            long duration = -5L;
            int reasonStart = 2;
            boolean durationCorrect = false;

            if (!args[1].equalsIgnoreCase("perm") && !args[1].equalsIgnoreCase("permanent")) {
                try {
                    duration = DateUtils.parseDateDiff(args[1], true);
                    durationCorrect = true;
                } catch (Exception e) {
                    reasonStart = 1;
                }
            }
            if (reasonStart == 2 && !durationCorrect) {
                sender.sendMessage(Lang.WRONG_DATE_FORMAT);
                return;
            }

            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = reasonStart; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Muted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = new Punishment(targetData, PunishmentType.MUTE);
            punishment.setSilent(silent);
            if (duration != -5L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setEnteredDuration(args[1]);
            punishment.setLast(true);
            punishment.setAddedBy(sender.getUniqueId());
            punishment.setAddedByName(sender.getName());
            punishment.setIPRelative(true);
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            targetData.getPunishData().getPunishments().add(punishment);

            punishment.execute(sender);
            punishment.save();

            if (sender.isPlayer()) {
                Player player = sender.getPlayer();
                PlayerData playerData = PlayerManager.getPlayerData(player.getUniqueId());
                if (playerData == null) {
                    return;
                }
                PunishHistory punishHistory = new PunishHistory(sender.getName(), PunishmentType.MUTE);
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

            PunishModule.getInstance().getProfileManager().unloadData(target);
        });
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class BanCommand extends BaseCommand {

    @Command(name = "ban", permission = Permission.PUNISHMENT_BAN, aliases = {"tempban"}, usage = "[-s] <player> [duration] <reason>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length < 2) {
            return CommandResult.INVALID_ARGS;
        }
        Tasks.runAsync(() -> {

            OfflinePunishData data = new OfflinePunishData(args[0]);
            data.load();

            if (data.isBanned()) {
                Logger.debug("Target is already banned");
                sender.sendMessage(Lang.ALREADY_BANNED.getMsg(data.getName()));
                return;
            }

            long duration = -5L;
            int reasonStart = 2;
            boolean durationCorrect = false;

            if (args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent")) {
                duration = -5L;
            } else {
                try {
                    duration = DateUtils.parseDateDiff(args[1], true);
                    durationCorrect = true;
                } catch (Exception e) {
                    reasonStart = 1;
                }
            }
            Logger.debug("Duration: %1", duration);
            if (reasonStart == 2 && !durationCorrect) {
                Logger.debug("Invalid date format!");
                sender.sendMessage(Lang.WRONG_DATE_FORMAT.toString());
                return;
            }
            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = reasonStart; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Banned");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (silent) {
                reason = reason.replace("-silent", "").replace("-s", "").trim();
            }
            Logger.debug("Silent: %1", silent);

            Punishment punishment = new Punishment(data, PunishmentType.BAN);
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
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            punishment.execute(sender);
            Logger.debug("Saving punishment: %1", punishment);
            punishment.save();
        });
        return CommandResult.SUCCESS;
    }

}

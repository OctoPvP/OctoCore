package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteIPCommand {
    @Command(name = "muteip", aliases = {"tempipmute", "ipmute", "tempmuteip"}, usage = "<player> [duration] <reason> [-s]")
    @Disable
    @Permission(Permissions.PUNISHMENT_MUTE)
    public CommandResult execute(CommandSender sender, String[] args) {
        if (true) {
            sender.sendMessage(CC.RED + "This command is still being implemented.");
            return CommandResult.SUCCESS;
        }
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /muteip <player> [duration] <reason> [-s]"));
                return;
            }

            OfflinePunishData data = new OfflinePunishData(args[0]);
            data.load();

            if (data.isMuted()) {
                sender.sendMessage(Lang.MUTE_ALREADY_MUTED.toString());
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
                sender.sendMessage(Lang.WRONG_DATE_FORMAT.toString());
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

            Punishment punishment = new Punishment(data, PunishmentType.MUTE);
            punishment.setSilent(silent);
            if (duration != -5L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setEnteredDuration(args[1]);
            punishment.setLast(true);
            if (sender instanceof Player) {
                UUID uuid = ((Player) sender).getUniqueId();
                punishment.setAddedBy(uuid);
            } else {
                punishment.setAddedBy(new UUID(0, 0));
            }
            punishment.setAddedByName(sender.getName());
            punishment.setIPRelative(true);
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            punishment.execute(sender);
            punishment.save();

        });
        return CommandResult.SUCCESS;
    }
}

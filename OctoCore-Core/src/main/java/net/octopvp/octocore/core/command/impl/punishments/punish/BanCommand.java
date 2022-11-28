package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;

public class BanCommand {
    @Command(name = "ban", aliases = {"tempban"})
    @Permission(Permissions.PUNISHMENT_BAN)
    public CommandResult execute(Sender sender, @Switch(value = "s", aliases = "silent") boolean silent, @Name("player") OfflinePunishData data, @Duration(allowPermanent = true, defaultValue = "perm") @Optional long duration, @JoinStrings String reason, @GetArgumentFor(1) String durationString) {
        Tasks.runAsync(() -> {
            data.load();

            if (data.isBanned()) {
                Logger.debug("Target is already banned");
                sender.sendMessage(Lang.ALREADY_BANNED.getMsg(data.getName()));
                return;
            }

            Logger.debug("Silent: %1, DurationString: %2", silent, durationString);

            Punishment punishment = new Punishment(data, PunishmentType.BAN);
            punishment.setSilent(silent);
            if (duration != -1L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setEnteredDuration(durationString);
            punishment.setLast(true);
            punishment.setAddedBy(sender.getUniqueId());
            punishment.setAddedByName(sender.getName());
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);
            punishment.setTargetAddress(data.getAddress());

            punishment.execute(sender);
            Logger.debug("Saving punishment: %1", punishment);
            punishment.save();
        });
        return CommandResult.SUCCESS;
    }

}

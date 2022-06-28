package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class WarnCommand {

    @Command(name = "warn", usage = "<player> [duration] <reason> [-s]")
    @Permission(Permissions.PUNISHMENT_WARN)
    public CommandResult execute(Sender sender, @Name("player") OfflinePunishData data, @Switch(value = "s", aliases = "silent") boolean silent, @Duration(allowPermanent = true, defaultValue = "perm") @Optional long duration, @JoinStrings String reason, @GetArgumentFor(1) String durationString) {
        Tasks.runAsync(() -> {
            data.load();

            Punishment punishment = new Punishment(data, PunishmentType.WARN);
            punishment.setSilent(silent);
            if (duration != -5L) {
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

            punishment.execute(sender);
            punishment.save();
        });
        return CommandResult.SUCCESS;
    }
}

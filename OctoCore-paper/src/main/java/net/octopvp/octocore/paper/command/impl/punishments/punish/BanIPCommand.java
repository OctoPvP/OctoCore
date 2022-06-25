package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class BanIPCommand {
    @Command(name = "banip", aliases = {"tempbanip", "ipban", "iptempban"})
    @Permission(Permissions.PUNISHMENT_IPBAN)
    public CommandResult execute(Sender sender, @Switch(value = "s", aliases = "silent") boolean silent, @Name("player") OfflinePunishData data, @Duration(allowPermanent = true, defaultValue = "perm") @Optional long duration, @JoinStrings String reason, @GetArgumentFor(1) String durationString) {
        Tasks.runAsync(() -> {
            data.load();

            if (data.isBanned()) {
                sender.sendMessage(Lang.ALREADY_BANNED.toString().replace("%name%", data.getName()));
                return;
            }

            Punishment punishment = new Punishment(data, PunishmentType.BAN);
            punishment.setSilent(silent);
            if (duration != -5L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setIPRelative(true);
            punishment.setEnteredDuration(durationString);
            punishment.setLast(true);
            punishment.setAddedByName(sender.getName());
            punishment.setAddedBy(sender.getUniqueId());
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            punishment.execute(sender);
            punishment.save();
        });
        return CommandResult.SUCCESS;
    }
}

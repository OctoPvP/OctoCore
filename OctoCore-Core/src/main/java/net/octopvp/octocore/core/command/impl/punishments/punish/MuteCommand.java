package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;

public class MuteCommand {
    @Command(name = "mute", aliases = "tempmute", usage = "<player> [duration] <reason> [-s]")
    @Permission(Permissions.PUNISHMENT_MUTE)
    public CommandResult execute(CommandSender sender, @Name("player") OfflinePunishData data, @Switch(value = "s", aliases = "silent") boolean silent, @Duration(allowPermanent = true, defaultValue = "perm") @Optional long duration, @JoinStrings String reason, @GetArgumentFor(1) String durationString) {
        Tasks.runAsync(() -> {
            data.load();

            if (data.isMuted()) {
                sender.sendMessage(Lang.MUTE_ALREADY_MUTED.toString());
                return;
            }

            Punishment punishment = new Punishment(data, PunishmentType.MUTE);
            punishment.setSilent(silent);
            if (duration != -1L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setEnteredDuration(durationString);
            PunishmentCommands.handlePunishmentMeta(sender, reason, punishment);
        });
        return CommandResult.SUCCESS;
    }
}

package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand {
    @Command(name = "ban", aliases = {"tempban"})
    @Permission(Permissions.PUNISHMENT_BAN)
    public CommandResult execute(CommandSender sender,
                                 @Switch(value = "s", aliases = "silent") boolean silent,
                                 @Name("player") OfflinePunishData data,
                                 @Duration(allowPermanent = true, defaultValue = "perm") @Name("duration") @Optional long duration,
                                 @JoinStrings @Name("reason") String reason,
                                 @GetArgumentFor(1) @Name("duration") String durationString
    ) {
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
            PunishmentCommands.handlePunishmentMeta(sender, reason, punishment);
        });
        return CommandResult.SUCCESS;
    }

}

package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.runnable.Tasks;

import java.util.Random;

public class KickCommand {

    private static final String[] errorMessages = {
            "java.net.ConnectException: Connection timed out: no further information:",
            "java.net.SocketTimeoutException: Read timed out",
            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host",
            "Internal Exception: java.net.SocketException: Connection reset"
    };

    @Command(name = "kick", aliases = {"kickplayer"}, usage = "<player> <reason> [-s]")
    @Permission(Permissions.PUNISHMENT_KICK)
    public void execute(Sender sender, @Switch(value = "s", aliases = "silent") boolean silent, OfflinePunishData data, @JoinStrings String reason) {
        Tasks.runAsync(() -> {
            data.load();

            String finalReason = reason;
            if (finalReason.contains("<error>")) {
                Random random = new Random();
                finalReason = finalReason.replace("<error>", errorMessages[random.nextInt(errorMessages.length)]);
            }

            Punishment punishment = new Punishment(data, PunishmentType.KICK);
            punishment.setSilent(silent);
            punishment.setPermanent(false);
            punishment.setIPRelative(false);
            punishment.setLast(true);
            punishment.setAddedByName(sender.getName());
            punishment.setAddedBy(sender.getUniqueId());
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(finalReason);

            punishment.execute(sender);
            punishment.save();
        });
    }
}

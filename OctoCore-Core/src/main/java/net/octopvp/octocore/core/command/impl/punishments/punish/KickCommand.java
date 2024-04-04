package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import org.bukkit.command.CommandSender;

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
    @Async
    public void execute(CommandSender sender, @Switch(value = "s", aliases = "silent") boolean silent, OfflinePunishData data, @JoinStrings String reason) {
        data.load();

        String finalReason = reason;
        if (finalReason.contains("<error>")) {
            Random random = new Random();
            finalReason = finalReason.replace("<error>", errorMessages[random.nextInt(errorMessages.length)]);
        }

        Punishment punishment = new Punishment(data, PunishmentType.KICK);
        punishment.setSilent(silent);
        punishment.setPermanent(false);
        punishment.setIpRelative(false);
        PunishmentCommands.handlePunishmentMeta(sender, finalReason, punishment);
    }

}

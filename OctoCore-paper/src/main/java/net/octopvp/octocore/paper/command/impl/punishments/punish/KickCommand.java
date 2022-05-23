package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Random;

public class KickCommand {

    private static final String[] errorMessages = {
            "java.net.ConnectException: Connection timed out: no further information:",
            "java.net.SocketTimeoutException: Read timed out",
            "Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host",
            "Internal Exception: java.net.SocketException: Connection reset"
    };

    @Command(name = "kick", aliases = {"kickplayer"})
    @Permission(Permissions.PUNISHMENT_KICK)
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /kick <player> <reason> [-s]"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            OfflinePunishData data = new OfflinePunishData(args[0]);
            data.load();

            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                String arg = args[i];
                if (arg.startsWith("<error")) { // <error> will send a random error message and <error:index> will send a specific error message from array
                    if (arg.equalsIgnoreCase("<error>"))
                        reasonBuilder.append(errorMessages[new Random().nextInt(errorMessages.length)]).append(" ");
                    else {
                        String errorIndex = arg.replace("<error:", "").replace(">", "");
                        try {
                            int index = Integer.parseInt(errorIndex);
                            if (index < 0 || index >= errorMessages.length)
                                throw new NumberFormatException("fuck you");
                            reasonBuilder.append(errorMessages[index]).append(" ");
                        } catch (NumberFormatException e) {
                            sender.sendMessage(CC.translate("&cInvalid index! Possible values: "));
                            for (int i1 = 0; i1 < errorMessages.length; i1++) {
                                sender.sendMessage(CC.translate("&c" + i1 + " - " + errorMessages[i1]));
                            }
                        }
                    }
                } else
                    reasonBuilder.append(arg).append(" ");
            }

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = new Punishment(data, PunishmentType.KICK);
            punishment.setSilent(silent);
            punishment.setPermanent(false);
            punishment.setIPRelative(false);
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

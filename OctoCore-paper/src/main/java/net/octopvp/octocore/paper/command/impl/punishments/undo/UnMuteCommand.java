package net.octopvp.octocore.paper.command.impl.punishments.undo;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.player.UndoPunishmentPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class UnMuteCommand extends BaseCommand {

    @Command(name = "unmute", usage = "<player> <reason>", permission = Permission.PUNISHMENT_UNMUTE)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length < 2) {
            return CommandResult.INVALID_ARGS;
        }
        Tasks.runAsync(() -> {
            OfflinePunishData data = new OfflinePunishData(args[0]);
            data.load();

            if (!data.isMuted()) {
                sender.sendMessage(Lang.MUTE_NOT_MUTED.getMsg(data.getName()));
                return;
            }

            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("unmuted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (silent) {
                reason = reason.replace("-silent", "").replace("-s", "").trim();
            }

            Punishment punishment = data.getActiveMute();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            String coloredSenderName;
            if (sender.isPlayer()) {
                coloredSenderName = PlayerManager.getInstance().getFormattedName(sender.getPlayer().getName());
            } else {
                coloredSenderName = "&4&lConsole";
            }

            new UndoPunishmentPacket(PunishmentType.MUTE, sender.getDisplayName(), coloredSenderName, sender.getName(), data.getName(), reason.trim(), silent).send();

            punishment.save(true);
        });
        return CommandResult.SUCCESS;
    }
}

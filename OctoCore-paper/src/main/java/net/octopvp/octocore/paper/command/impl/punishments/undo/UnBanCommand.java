package net.octopvp.octocore.paper.command.impl.punishments.undo;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.player.UndoPunishmentPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class UnBanCommand {
    @Command(name = "unban")
    @Permission(Permissions.PUNISHMENT_UNBAN)
    public CommandResult execute(Sender sender, String player, @JoinStrings @Optional @Name("reason") String r) {
        Tasks.runAsync(() -> {
            OfflinePunishData data = new OfflinePunishData(player);
            data.load();

            if (!data.isBanned()) {
                sender.sendMessage(Lang.NOT_BANNED.getMsg(data.getName()));
                return;
            }

            String reason = r == null ? "No reason provided." : r;

            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (silent) {
                reason = reason.replace("-silent", "").replace("-s", "").trim();
            }

            IPunishment punishment = data.getActiveBan();
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

            new UndoPunishmentPacket(PunishmentType.BAN, sender.getDisplayName(), coloredSenderName, sender.getName(), data.getName(), reason.trim(), silent).send();

            punishment.save(true);
        });
        return CommandResult.SUCCESS;
    }
}

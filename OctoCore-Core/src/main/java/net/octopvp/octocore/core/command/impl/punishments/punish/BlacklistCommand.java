package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;

public class BlacklistCommand {
    @Command(name = "blacklist", aliases = {"bl", "blplayer", "blacklistplayer"}, usage = "<player> <reason> [-s]")
    @Permission(Permissions.PUNISHMENT_BLACKLIST)
    public CommandResult execute(Sender sender, OfflinePunishData data, @JoinStrings String reason, @Switch boolean silent) {
        Tasks.runAsync(() -> {
            data.load();

            if (data.isBlacklisted()) {
                sender.sendMessage(Lang.BLACKLIST_ALREADY_BLACKLISTED.toString().replace("%name%", data.getName()));
                return;
            }

            Punishment punishment = new Punishment(data, PunishmentType.BLACKLIST);
            punishment.setSilent(silent);
            punishment.setPermanent(true);
            punishment.setIPRelative(true);
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

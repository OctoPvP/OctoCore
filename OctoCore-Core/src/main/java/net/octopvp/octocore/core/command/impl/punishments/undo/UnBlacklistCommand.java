package net.octopvp.octocore.core.command.impl.punishments.undo;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.player.UndoPunishmentPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnBlacklistCommand {
    @Command(name = "unblacklist", aliases = {"unbl", "unblplayer", "unblacklistplayer"})
    @Permission(Permissions.PUNISHMENT_UNBLACKLIST)
    @PlayerOnly
    public CommandResult execute(CommandSender sender, String player, @JoinStrings @Optional @Name("reason") String r) {
        Tasks.runAsync(() -> {
            OfflinePunishData data = new OfflinePunishData(player);
            data.load();

            if (!data.isBlacklisted()) {
                sender.sendMessage(Lang.BLACKLIST_NOT_BLACKLISTED.getMsg(data.getName()));
                return;
            }

            String reason = r == null ? "No reason provided." : r;

            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (silent) {
                reason = reason.replace("-silent", "").replace("-s", "").trim();
            }

            IPunishment punishment = data.getActiveBlacklist();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            String coloredSenderName, displayName;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                coloredSenderName = PlayerManager.getInstance().getFormattedName(p.getName());
                displayName = p.getDisplayName();
            } else {
                coloredSenderName = "&4&lConsole";
                displayName = "Console";
            }

            new UndoPunishmentPacket(PunishmentType.BLACKLIST, displayName, coloredSenderName, sender.getName(), data.getName(), reason.trim(), silent).send();

            punishment.save(true);
        });
        return CommandResult.SUCCESS;
    }
}

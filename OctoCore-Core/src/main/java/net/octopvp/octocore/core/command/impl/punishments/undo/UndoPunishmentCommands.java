package net.octopvp.octocore.core.command.impl.punishments.undo;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.database.redis.packets.player.UndoPunishmentPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UndoPunishmentCommands {
    @Async
    @Command(name = "unban")
    @Permission(Permissions.PUNISHMENT_UNBAN)
    public void unban(CommandSender sender, @Name("player") OfflinePunishData data, @Switch(value = "silent", aliases = "s") boolean silent, @JoinStrings @Optional @Name("reason") String r) {
        data.load();

        if (!data.isBanned()) {
            if (data.isBlacklisted()) sender.sendMessage(Lang.NOT_BANNED_IS_BLACKLSITED.getMsg(data.getName()));
            else sender.sendMessage(Lang.NOT_BANNED.getMsg(data.getName()));
            return;
        }

        String reason = r == null ? "No reason provided." : r;

        IPunishment punishment = data.getActiveBan();
        undoPunishment(sender, silent, reason, punishment, data, PunishmentType.BAN);
    }

    @Async
    @Command(name = "unmute")
    @Permission(Permissions.PUNISHMENT_UNMUTE)
    public void unmute(CommandSender sender, @Name("player") OfflinePunishData data, @Switch(value = "silent", aliases = "s") boolean silent, @JoinStrings @Optional @Name("reason") String r) {
        data.load();

        if (!data.isMuted()) {
            sender.sendMessage(Lang.MUTE_NOT_MUTED.getMsg(data.getName()));
            return;
        }

        String reason = r == null ? "No reason provided." : r;

        IPunishment punishment = data.getActiveMute();
        undoPunishment(sender, silent, reason, punishment, data, PunishmentType.MUTE);
    }

    @Async
    @Command(name = "unblacklist", aliases = {"unbl", "unblplayer", "unblacklistplayer"})
    @Permission(Permissions.PUNISHMENT_UNBLACKLIST)
    public void unblacklist(CommandSender sender, @Name("player") OfflinePunishData data, @Switch(value = "silent", aliases = "s") boolean silent, @JoinStrings @Optional @Name("reason") String r) {
        data.load();

        if (!data.isBlacklisted()) {
            sender.sendMessage(Lang.BLACKLIST_NOT_BLACKLISTED.getMsg(data.getName()));
            return;
        }

        String reason = r == null ? "No reason provided." : r;

        IPunishment punishment = data.getActiveBlacklist();
        undoPunishment(sender, silent, reason, punishment, data, PunishmentType.BLACKLIST);
    }

    private void undoPunishment(CommandSender sender, @Switch(value = "silent", aliases = "s") boolean silent, String reason, IPunishment punishment, IPunishData data, PunishmentType type) {
        if (sender instanceof Player) {
            UUID uuid = ((Player) sender).getUniqueId();
            punishment.setRemovedById(uuid);
        } else {
            punishment.setRemovedById(new UUID(0, 0));
        }
        punishment.setActive(false);
        punishment.setLast(false);
        punishment.setRemovedBy(sender.getName());
        punishment.setRemovedFor(reason);
        punishment.setRemovedSilent(silent);
        punishment.setWhenRemoved(System.currentTimeMillis());

        String coloredSenderName;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            coloredSenderName = PlayerManager.getInstance().getFormattedName(p.getName());
        } else {
            coloredSenderName = "&4&lConsole";
        }
        new UndoPunishmentPacket(type, coloredSenderName, /*sender.getName(),*/data.getName(), reason.trim(), silent).send();
        punishment.save();
    }
}

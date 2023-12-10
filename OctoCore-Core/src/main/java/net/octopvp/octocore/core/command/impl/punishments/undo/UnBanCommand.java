package net.octopvp.octocore.core.command.impl.punishments.undo;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.player.UndoPunishmentPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnBanCommand {
    @Command(name = "unban")
    @Permission(Permissions.PUNISHMENT_UNBAN)
    public CommandResult execute(CommandSender sender, String player, @JoinStrings @Optional @Name("reason") String r) {
        Tasks.runAsync(() -> {
            OfflinePunishData data = new OfflinePunishData(player);
            data.load();

            if (!data.isBanned()) {
                if (data.isBlacklisted())
                    sender.sendMessage(Lang.NOT_BANNED_IS_BLACKLSITED.getMsg(data.getName()));
                else
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
            if (sender instanceof Player) {
                UUID uuid = ((Player) sender).getUniqueId();
                punishment.setRemovedById(uuid);
            } else {
                punishment.setRemovedById(new UUID(0, 0));
            }
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

            new UndoPunishmentPacket(PunishmentType.BAN/*, displayName*/, coloredSenderName, /*sender.getName(),*/ data.getName(), reason.trim(), silent).send();

            punishment.save(true);
        });
        return CommandResult.SUCCESS;
    }
}

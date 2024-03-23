package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanIPCommand {
    @SuppressWarnings("unused")
    @Command(name = "banip", aliases = { "tempbanip", "ipban", "iptempban" })
    @Permission(Permissions.PUNISHMENT_IPBAN)
    public CommandResult execute(CommandSender sender, @Switch(value = "s", aliases = "silent") boolean silent,
            // instead of player use a raw ip address as an argument
            @Name("ipAddress") @Optional String ipAddress,
            @Name("player") @Optional String name,
            @Duration(allowPermanent = true, defaultValue = "perm") @Optional long duration, @JoinStrings String reason,
            @GetArgumentFor(1) String durationString) {
        Tasks.runAsync(() -> {
            OfflinePunishData data = new OfflinePunishData(name, ipAddress);
            if (data != null) {
                // Handle the case where a player name was provided
                data.load();
            } else if (ipAddress != null) {
                // Handle the case where an IP address was provided
                // You might need to create a new OfflinePunishData instance with the IP address
                data = new OfflinePunishData(ipAddress);
            } else {
                // Neither a player name nor an IP address was provided
                // You might want to send an error message to the sender
                sender.sendMessage("You must provide either a player name or an IP address.");
                return;
            }
            //data.load();

            if (data.isBanned()) {
                sender.sendMessage(Lang.ALREADY_BANNED.toString().replace("%name%", data.getName()));
                return;
            }

            Punishment punishment = new Punishment(data, PunishmentType.BAN);
            punishment.setSilent(silent);
            if (duration != -1L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setIPRelative(true);
            // if user is using only ipaddress argument then set target address to ipaddress
            if (data.getName() == null) {
                punishment.setTargetAddress(ipAddress);
            } else {
                punishment.setTargetAddress(data.getAddress());
            }
            //punishment.setTargetAddress(String.valueOf(data.getAddress()));
            punishment.setEnteredDuration(durationString);
            punishment.setLast(true);
            punishment.setAddedByName(sender.getName());
            if (sender instanceof Player) {
                UUID uuid = ((Player) sender).getUniqueId();
                punishment.setAddedBy(uuid);
            } else {
                punishment.setAddedBy(new UUID(0, 0));
            }
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            punishment.execute(sender);
            punishment.save();
        });
        return CommandResult.SUCCESS;
    }
}

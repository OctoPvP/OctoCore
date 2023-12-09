package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishmentCommands {
    static void handlePunishmentMeta(CommandSender sender, String finalReason, Punishment punishment) {
        punishment.setLast(true);
        if (sender instanceof Player) {
            UUID uuid = ((Player) sender).getUniqueId();
            punishment.setAddedBy(uuid);
            punishment.setAddedByName(sender.getName());
        } else {
            punishment.setAddedBy(new UUID(0, 0));
            punishment.setAddedByName("Console");
        }
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setReason(finalReason);

        punishment.execute(sender);
        punishment.save();
    }
}
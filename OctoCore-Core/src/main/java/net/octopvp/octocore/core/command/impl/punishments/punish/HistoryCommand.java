package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.module.impl.punishments.menus.HistoryMenu;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;

public class HistoryCommand {
    @Command(name = "history", aliases = {"c", "cpunishments", "checkpunishments", "hist", "check"})
    @Permission(Permissions.PUNISHMENT_HISTORY)
    public CommandResult execute(@Sender Player sender, @Name("player") PlayerData data) {
        data.getPunishData().load();
        data.loadAlts(data.getLastSeenIp());
        Tasks.runSync(() -> new HistoryMenu(data).open(sender));
        return CommandResult.SUCCESS;
    }
}

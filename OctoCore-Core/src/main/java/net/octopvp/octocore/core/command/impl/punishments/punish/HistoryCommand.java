package net.octopvp.octocore.core.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.menus.HistoryMenu;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bson.Document;
import org.bukkit.entity.Player;

public class HistoryCommand {
    @Command(name = "history", aliases = {"c", "cpunishments", "checkpunishments", "hist", "check"})
    @Permission(Permissions.PUNISHMENT_HISTORY)
    public CommandResult execute(@Sender Player sender, @Name("player") OfflinePunishData data) {
        Tasks.runAsync(() -> {
            //Document document = PlayerManager.getInstance().getDocument(target);
            //OfflinePunishData data = new OfflinePunishData(target).load(false).loadAlts(document);
            data.load(false);
            new HistoryMenu(data).open(sender);
        });
        return CommandResult.SUCCESS;
    }
}

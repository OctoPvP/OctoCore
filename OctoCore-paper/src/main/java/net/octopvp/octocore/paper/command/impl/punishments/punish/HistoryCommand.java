package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.menus.HistoryMenu;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bson.Document;

public class HistoryCommand {
    @Command(name = "history", aliases = {"c", "cpunishments", "checkpunishments", "hist", "check"})
    @Permission(Permissions.PUNISHMENT_HISTORY)
    public CommandResult execute(Sender sender, String target) {
        Tasks.runAsync(() -> {
            Document document = PlayerManager.getInstance().getDocument(target);
            OfflinePunishData data = new OfflinePunishData(target).load(false).loadAlts(document);
            new HistoryMenu(data).open(sender);
        });
        return CommandResult.SUCCESS;
    }
}

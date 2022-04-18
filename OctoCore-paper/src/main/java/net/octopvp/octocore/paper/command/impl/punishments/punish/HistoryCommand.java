package net.octopvp.octocore.paper.command.impl.punishments.punish;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.menus.HistoryMenu;
import net.octopvp.octocore.paper.objects.OfflinePunishData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bson.Document;

public class HistoryCommand extends BaseCommand {

    @Command(name = "history", permission = Permission.PUNISHMENT_HISTORY, aliases = {"c", "cpunishments", "checkpunishments", "hist", "check"})
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length == 0) {
                sender.sendMessage(CC.translate("&cUsage: /history <player>"));
                return;
            }

            Document document = PlayerManager.getInstance().getDocument(args[0]);
            OfflinePunishData data = new OfflinePunishData(args[0]).load(false).loadAlts(document);
            new HistoryMenu(data).open(sender);
        });
        return CommandResult.SUCCESS;
    }
}

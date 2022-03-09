package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class DisplayNameCommand extends BaseCommand {
    @Command(name = "whatsmydisplayname", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(sender.getPlayer().getDisplayName());
        PlayerData data = PlayerManager.getData(sender.getPlayer());
        sender.sendMessage(data.getCurrentColor() + sender.getPlayer().getDisplayName());
        return CommandResult.SUCCESS;
    }
}
